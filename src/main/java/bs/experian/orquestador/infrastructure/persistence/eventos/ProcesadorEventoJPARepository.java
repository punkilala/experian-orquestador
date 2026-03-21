package bs.experian.orquestador.infrastructure.persistence.eventos;


import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import bs.experian.orquestador.application.model.evento.EventoProcesadoDto.Documento;
import bs.experian.orquestador.infrastructure.persistence.eventos.entity.EventoExperianVivoEntity;
import bs.experian.orquestador.infrastructure.persistence.eventos.entity.EventosExperianHistEntity;
import bs.experian.orquestador.infrastructure.persistence.eventos.repository.EventoExperianHistRepository;
import bs.experian.orquestador.infrastructure.persistence.eventos.repository.EventoExperianVivoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProcesadorEventoJPARepository {
	
	private final EventoExperianVivoRepository eventoExperianVivoRepository;
	private final EventoExperianHistRepository eventoExperianHistRepository;
	
	/**
	 * Al recibir un nuevo evento de Experian se inserta en la cola de trabajo del worker
	 * @param evento
	 * @param origenEvento
	 * @param payloadJson
	 */
	@Transactional
	public void encolarEvento(EventoDto evento, String origenEvento, String payloadJson) {	
		
        EventoExperianVivoEntity entity = EventoExperianVivoEntity.builder()
                .queryId(evento.getQueryId())
                .notificationId(evento.getNotificationId())
                .origenEvento(origenEvento)
                .eventType(evento.getEventType())
                .payloadJson(payloadJson)
                .estadoTecnico("PENDIENTE")
                .intentos(0)
                .fechaAlta(OffsetDateTime.now())
                .build();

        eventoExperianVivoRepository.save(entity);
    }
	
	
	/**
	 * reprogramar evento que por error tecnico-- conexiones caidas etc... no se ha podido procesar.
	 * @param eventoId
	 * @param error
	 * @param errorMesg
	 */
	@Transactional
	public void reprogramarEvento(Long eventoId, String error, String errorMesg) {
		
		Optional<EventoExperianVivoEntity> optional = eventoExperianVivoRepository.findById(eventoId);

		if (optional.isEmpty()) {
		    log.error("ERR EXPERIAN: Inconsistencia: evento {} no encontrado en tabla viva al reprogramar", eventoId);
		    return;
		}

		EventoExperianVivoEntity entity = optional.get();

		entity.setEstadoTecnico("PENDIENTE");
		entity.setIntentos(entity.getIntentos() + 1);
		entity.setNextRetry(OffsetDateTime.now().plusHours(1));
		entity.setProcesoDesde(null);
		entity.setErrorCode(error);
		entity.setErrorMensaje(errorMesg);

		eventoExperianVivoRepository.save(entity);
    }
	
	/**
	 * Mover el evento de la cola de trabajo del worker al historico
	 * @param eventoCola
	 * @param dto
	 */
	public void moverEventoProcesadoToHist(EventoExperianVivoEntity eventoCola,EventoProcesadoDto dto, String procesado) {
		
		if(null == dto.getDocumento()) {
			dto.setDocumento(new Documento());
		}
		
		String payload = eventoCola.getPayloadJson();
		if (payload.contains("\"jsonDocument\"")) {
		    payload = payload.replaceAll(
		            "(?s)\"jsonDocument\"\\s*:\\s*\"\\{.*?\\}\"",
		            "\"jsonDocument\" : \"true\""
		    );
		}
		EventosExperianHistEntity entity = new EventosExperianHistEntity();
		entity.setQueryId(eventoCola.getQueryId());
		entity.setNotificationId(eventoCola.getNotificationId());
		entity.setOrigenEvento(eventoCola.getOrigenEvento());
		entity.setEventType(eventoCola.getEventType());
		entity.setEstadoExperian(dto.getEstadoExperian());
		entity.setSubestadoExperian(dto.getSubestadoExperian());
		entity.setDocumentCode(dto.getDocumento().getDocumentCode());
		entity.setPayloadJson(payload);
		entity.setFechaAlta(eventoCola.getFechaAlta());
		entity.setFechaProcesado(OffsetDateTime.now());
		entity.setErrorCode(eventoCola.getErrorCode());
		entity.setResultadoProceso(procesado);
		entity.setErrorMensaje(eventoCola.getErrorMensaje());
		
		eventoExperianHistRepository.save(entity);	
	}
	
	/**
	 * mover a historico evento que no se puede encolar
	 * 
	 */
	public void moverEventoNoProcesadoToHist(EventoDto request, String origen, String payload,
			String resultado, String errCode, String errMsj) {
		
		EventosExperianHistEntity entity = new EventosExperianHistEntity();
		entity.setQueryId(request.getQueryId());
		entity.setNotificationId(request.getNotificationId());
		entity.setOrigenEvento(origen);
		entity.setEventType(request.getEventType());
		entity.setPayloadJson(payload);
		entity.setFechaAlta(OffsetDateTime.now());
		entity.setFechaProcesado(OffsetDateTime.now());
		entity.setResultadoProceso(resultado);
		entity.setErrorCode(errCode);
		entity.setErrorMensaje(errMsj);
		
		eventoExperianHistRepository.save(entity);
		
	}
	
	/**
	 * Borrar evento procesado de la cola de trabajo del worker
	 * @param idEvento
	 */
	public void borraEventoDeColaWorker (Long idEvento) {
		eventoExperianVivoRepository.deleteById(idEvento);
	}
	
	/**
	 * eventos con error no recuperable
	 * @param evento
	 * @param eventoProcesadoDto
	 * @param procesado
	 */
	@Transactional
	public void eventoNoProcesadoErrorFuncional (EventoExperianVivoEntity evento, EventoProcesadoDto eventoProcesadoDto, String procesado) {
		//mover a historico
		moverEventoProcesadoToHist(evento, eventoProcesadoDto, procesado);
		//borrar de cola de trabajo
		borraEventoDeColaWorker(evento.getId());
			
	}
	
	
    
	

}
