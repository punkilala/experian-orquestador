package bs.experian.orquestador.infrastructure.persistence.eventos;


import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import bs.experian.orquestador.application.model.evento.EventoProcesadoDto.Documento;
import bs.experian.orquestador.infrastructure.persistence.eventos.entity.EventoExperianErrorEntity;
import bs.experian.orquestador.infrastructure.persistence.eventos.entity.EventoExperianVivoEntity;
import bs.experian.orquestador.infrastructure.persistence.eventos.entity.EventosExperianHistEntity;
import bs.experian.orquestador.infrastructure.persistence.eventos.repository.EventoExperianErrorRepository;
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
	private final EventoExperianErrorRepository eventoExperianErrorRepository;
	
	/**
	 * Al recibir un nuevo evento de Experian se inserta en la cola de trabajo del worker
	 * @param evento
	 * @param origenEvento
	 * @param payloadJson
	 */
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
	public void moverEventoProcesadoToHist(EventoExperianVivoEntity eventoCola,EventoProcesadoDto dto) {
		
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
		EventosExperianHistEntity eventoHist = new EventosExperianHistEntity(
				eventoCola.getId(),
				eventoCola.getQueryId(),
				eventoCola.getNotificationId(),
				eventoCola.getOrigenEvento(),
				eventoCola.getEventType(),
				dto.getEstadoExperian(),
				dto.getSubestadoExperian(),
				dto.getDocumento().getDocumentCode(),
				payload,
				eventoCola.getFechaAlta(),
				OffsetDateTime.now(),
				"PROCESADO",
				eventoCola.getErrorCode(),
				eventoCola.getErrorMensaje()			
		);
		
		eventoExperianHistRepository.save(eventoHist);	
	}
	
	/**
	 * Borrar evento procesado de la cola de trabajo del worker
	 * @param idEvento
	 */
	public void borraEventoDeColaWorker (Long idEvento) {
		eventoExperianVivoRepository.deleteById(idEvento);
	}
	
	
	public void eventoNoProcesadoErrorFuncional (EventoExperianVivoEntity evento, EventoProcesadoDto eventoProcesadoDto) {
		//guardar en tabla de errores
		String documento = null;
		if(null != eventoProcesadoDto.getDocumento()) {
			documento = eventoProcesadoDto.getDocumento().getDocumentCode();
		}
		EventoExperianErrorEntity errorEntity = new EventoExperianErrorEntity(
				evento.getId(),
				evento.getQueryId(),
				evento.getNotificationId(),
				evento.getOrigenEvento(),
				evento.getEventType(),
				eventoProcesadoDto.getEstadoExperian(),
				eventoProcesadoDto.getSubestadoExperian(),
				documento,
				evento.getPayloadJson(),
				evento.getErrorCode(),
				evento.getErrorMensaje(),
				OffsetDateTime.now()
			);
		eventoExperianErrorRepository.save(errorEntity);
		
		//borrar de tabla viva
		borraEventoDeColaWorker(evento.getId());
			
	}
	
	
    
	

}
