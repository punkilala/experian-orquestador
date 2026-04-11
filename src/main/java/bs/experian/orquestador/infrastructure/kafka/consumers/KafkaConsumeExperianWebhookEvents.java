package bs.experian.orquestador.infrastructure.kafka.consumers;

import java.time.OffsetDateTime;
import java.util.List;

import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import bs.experian.orquestador.application.EventoApplicationService;
import bs.experian.orquestador.application.SolicitudApplicationService;
import bs.experian.orquestador.application.eventos.EventoProcesador;
import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.application.model.evento.PayLoadDto;
import bs.experian.orquestador.infrastructure.exceptions.NonRetryableProcessingException;
import bs.experian.orquestador.infrastructure.exceptions.RetryableProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static bs.experian.orquestador.application.utils.OrquestadorUtils.stackTraceToString;
import static bs.experian.orquestador.domain.constants.ExperianConstants.*;

/**
 * Topic kafka para consumir los eventos procedentes de Experian
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumeExperianWebhookEvents {
	
	private final ObjectMapper objectMapper;
	private final List<EventoProcesador> procesadores;
	private final EventoApplicationService eventoApplicationService;
	private final SolicitudApplicationService solicitudApplicationService;
	
	@RetryableTopic(
            attempts = "4", 
            backoff = @Backoff(delay = 3_600_000),
            exclude = {NonRetryableProcessingException.class},
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            autoCreateTopics = "true"
    )
    @KafkaListener(
            topics = {
            		"${app.kafka.topics.webhook-events}",
            		"${app.kafka.topics.descarga-resultado}",
            		"${app.kafka.topics.custodia-resultado}"
            },
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listener(
            String mensaje,
            @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String queryId,
            Acknowledgment ack) {
		
		EventoDto evento = null;
		
		try {
			log.info("Mensaje recibido en ORQUESTADOR: {}", mensaje);

	        evento = objectMapper.readValue(mensaje, EventoDto.class);
	        evento.getEventData().setFechaInicioEvento(OffsetDateTime.now());
	        
	        //saber si hay que atender al evento
	        solicitudApplicationService.comprobarSolicitud(evento);
	        
	        JsonNode node = objectMapper.readTree(mensaje);
	        String jsonBonito = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
	        evento.getEventData().setPayLoad(jsonBonito);
	        
			boolean procesado = false;
			
			for (EventoProcesador procesador : procesadores) {
				if(procesador.aplica(evento)) {
					procesador.procesar(evento);
					procesado = true;
					break;
				}
			}
			
			String result = procesado ? "PROCESADO" : "NO_ACCION";
			
			if(evento.getEventData().isEventoFinal()) {
				eventoApplicationService.finalizarSolicitud(evento, result);
			}else {
				eventoApplicationService.finalizarEvento(evento, result, null, null);
			}
			
			ack.acknowledge();
			
		 } catch (JsonProcessingException e) {
		        eventoApplicationService.finalizarEvento(evento, ERROR_PROCESAMIENTO, e.getMessage(), stackTraceToString(e, 30));
		        throw new NonRetryableProcessingException("Mensaje JSON invalido", e);
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
    		String mensaje,
            @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String queryId,
            @Header(name = "kafka_exception-message", required = false) byte[] exceptionMessage,
            @Header(name = "kafka_exception-stacktrace", required = false) byte[] exceptionStacktrace,
            Acknowledgment ack) {
    	
        String exceptionMsg = toStringSafe(exceptionMessage);
        String stacktrace = toStringSafe(exceptionStacktrace);
        
        EventoDto evento = null;
        
        log.error("###ERR @DLT KafkaConsumeExperianWebhookEvents; ErrorCode %s stacktrace %s".formatted(exceptionMsg, stacktrace));
        
        try {
        	evento = objectMapper.readValue(mensaje, EventoDto.class);
        	evento.getEventData().setFechaInicioEvento(OffsetDateTime.now());
            JsonNode node = objectMapper.readTree(mensaje);
        	String jsonBonito = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        	evento.getEventData().setPayLoad(jsonBonito);    
		} catch (JsonProcessingException e) {
			evento = new EventoDto();
			evento.setQueryId(UNKNOWN);
			evento.setNotificationId(UNKNOWN);
			evento.setEventType("no");
			
			PayLoadDto dto = new PayLoadDto();
			dto.setStatus(UNKNOWN);
			dto.setSubstatus(UNKNOWN);
			dto.setPayLoad(mensaje);
			evento.setEventData(dto);
		}

        eventoApplicationService.finalizarEvento(evento, ERROR_PROCESAMIENTO, exceptionMsg, stacktrace);
        ack.acknowledge();

    }
    
    private String toStringSafe(byte[] value) {
        return value != null
                ? new String(value, java.nio.charset.StandardCharsets.UTF_8)
                : null;
    }
}
