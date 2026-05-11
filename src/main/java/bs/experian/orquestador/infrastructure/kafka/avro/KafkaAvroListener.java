package bs.experian.orquestador.infrastructure.kafka.avro;

import static bs.experian.orquestador.application.utils.OrquestadorUtils.stackTraceToString;
import static bs.experian.orquestador.domain.constants.ExperianConstants.ERROR_PROCESAMIENTO;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import bs.experian.orquestador.application.EventoApplicationService;
import bs.experian.orquestador.application.SolicitudApplicationService;
import bs.experian.orquestador.application.eventos.EventoProcesador;
import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.application.model.evento.PayLoadDto;
import bs.experian.orquestador.infrastructure.exceptions.NonRetryableProcessingException;
import bs.experian.orquestador.infrastructure.exceptions.RetryableProcessingException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static bs.experian.orquestador.domain.constants.ExperianConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaAvroListener {
	private final List<TiposEventosProcesador> tiposEventos;
	private final List<EventoProcesador> procesadoresEvento;
	private final EventoApplicationService eventoApplicationService;
	private final SolicitudApplicationService solicitudApplicationService;
	 
	@PostConstruct
	void init() {
	    log.info("###########################KafkaAvroListener inicializado");
	}
	@RetryableTopic(
            attempts = "4", 
            backoff = @Backoff(delay = 3_600_000),
            exclude = {NonRetryableProcessingException.class},
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            autoCreateTopics = "true",
            kafkaTemplate = "avroKafkaTemplate"
    )
	@KafkaListener(
	        topics = {
	            "experian.statusChanged.avro.v3",
	            "experian.newDocumentAvailable.avro.v3",
	            "documento.descarga.result.avro.v1"
	        },
	        containerFactory = "avroKafkaListenerContainerFactory"
	)
	public void consumir(ConsumerRecord<String, Object>  recordKafka, Acknowledgment ack) {
		EventoDto evento = null;
		try {
			Object mensajeKafka = recordKafka.value();
			
			//obtener eventoDto a partir del mensaje kafka
			for (TiposEventosProcesador procesador : tiposEventos) {
				if(procesador.soportado(mensajeKafka)) {
					evento = procesador.procesar(mensajeKafka);
					evento.getEventData().setFechaInicioEvento(OffsetDateTime.now());
					break;
				}
			}
			if (null == evento) {
				throw new NonRetryableProcessingException("Evento no permitido");
			}
			
	        
	        //saber si hay que atender al evento
	        solicitudApplicationService.comprobarSolicitud(evento);
	        
	        boolean procesado = false;
			
	        //procesar evento
			for (EventoProcesador procesador : procesadoresEvento) {
				if(procesador.aplica(evento)) {
					procesador.procesar(evento);
					procesado = true;
					break;
				}
			}
			
			
			//finalizar evento
			if(evento.getEventData().isEventoFinal()) {
				eventoApplicationService.finalizarSolicitud(evento, "PROCESADO");
			}
			
			//si no hay procesamiento del evento se pasa a historico sin hacer nada mas
			if(!procesado){
				eventoApplicationService.finalizarEvento(evento, "NO_ACCION", null, null);
			}
			
			//marcar mensaje kafka como consumido
			ack.acknowledge();
			
		} catch ( KafkaException e) {
	        log.error("###ERR KafkaConsumeExperianWebhookEvents: Error Kafka procesando evento Experian", e);
	        eventoApplicationService.finalizarEvento(evento, ERROR_PROCESAMIENTO, e.getMessage(), stackTraceToString(e, 30));
	        throw new RetryableProcessingException("Error Kafka procesando evento Experian", e);
	    }catch (RetryableProcessingException e) {
	    	eventoApplicationService.finalizarEvento(evento, ERROR_PROCESAMIENTO, e.getMessage(), stackTraceToString(e, 30));
	        throw e;
	    }catch (NonRetryableProcessingException e) {
	        throw e;
	    } catch (Exception e) {
	    	eventoApplicationService.finalizarEvento(evento, ERROR_PROCESAMIENTO, e.getMessage(), stackTraceToString(e, 30));
	        log.error("###ERR KafkaConsumeExperianWebhookEvents: inesperado procesando evento Experian", stackTraceToString(e, 30));
	        throw new RetryableProcessingException("Error inesperado procesando evento Experian", e);
	    }       
    }
	@DltHandler
    public void dlt(
    		ConsumerRecord<String, Object> recordKafka,
            @Header(name = "kafka_exception-message", required = false) byte[] exceptionMessage,
            @Header(name = "kafka_exception-stacktrace", required = false) byte[] exceptionStacktrace,
            Acknowledgment ack) {
    	
        String exceptionMsg = toStringSafe(exceptionMessage);
        String stacktrace = toStringSafe(exceptionStacktrace);
        
        Object mensajeKafka = recordKafka.value();
        String queryId = recordKafka.key();
        
        EventoDto evento = null;
        
        log.error("###ERR @DLT KafkaConsumeExperianWebhookEvents; ErrorCode %s stacktrace %s".formatted(exceptionMsg, stacktrace));
        
        try {
        	
        	for (TiposEventosProcesador procesador : tiposEventos) {
                if (procesador.soportado(mensajeKafka)) {
                    evento = procesador.procesar(mensajeKafka);
                    break;
                }
            }
        	
        	
    	   if (evento == null) { 
               evento = new EventoDto();
               evento.setQueryId(queryId != null ? queryId : UNKNOWN);
               evento.setNotificationId(UNKNOWN);
               evento.setEventType("EVENTO_AVRO_NO_SOPORTADO");

               PayLoadDto dto = new PayLoadDto();
               dto.setStatus(UNKNOWN);
               dto.setSubstatus(UNKNOWN);
               dto.setPayLoad(String.valueOf(mensajeKafka));
               evento.setEventData(dto);
           }  
		} catch (Exception e) {
			evento = new EventoDto();
			evento.setQueryId(UNKNOWN);
			evento.setNotificationId(UNKNOWN);
			evento.setEventType("no");
			
			PayLoadDto dto = new PayLoadDto();
			dto.setStatus(UNKNOWN);
			dto.setSubstatus(UNKNOWN);
			dto.setPayLoad(mensajeKafka.toString());
			evento.setEventData(dto);
		}
        evento.getEventData().setFechaInicioEvento(OffsetDateTime.now());
        eventoApplicationService.finalizarEvento(evento, ERROR_PROCESAMIENTO, exceptionMsg, stacktrace);
        ack.acknowledge();

    }
    
    private String toStringSafe(byte[] value) {
        return value != null
                ? new String(value, java.nio.charset.StandardCharsets.UTF_8)
                : null;
    }
}
