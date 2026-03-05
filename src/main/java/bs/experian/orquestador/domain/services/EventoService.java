package bs.experian.orquestador.domain.services;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import bs.experian.orquestador.domain.enums.DomainEnum;
import bs.experian.orquestador.infrastructure.exceptions.AgoraException;
import bs.experian.orquestador.infrastructure.persistence.eventos.EventoExperianErrorEntity;
import bs.experian.orquestador.infrastructure.persistence.eventos.EventoExperianErrorRepository;
import bs.experian.orquestador.infrastructure.persistence.eventos.EventoExperianVivoEntity;
import bs.experian.orquestador.infrastructure.persistence.eventos.EventoExperianVivoRepository;
import bs.experian.orquestador.infrastructure.persistence.eventos.EventoExperianWorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventoService {
	
	private final ObjectMapper objectMapper;
	private final EventoExperianWorkerRepository eventoExperianWorkerRepository;
	private final EventoExperianErrorRepository eventoExperianErrorRepository;
	private final EventoExperianVivoRepository eventoExperianVivoRepository;

	
	/**
	 * Recibir un nuevo evento de experian y encolarlo en la tabla de trabajo del worker
	 * @param request
	 */
	public void recibirEventoExperian(EventoDto request) {
		String payload = "";
		try {
			payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
			//encolar evento recibido
			eventoExperianWorkerRepository.encolarEvento(request, DomainEnum.TiposEventosHistoricos.EXPERIAN.name(), payload);
		} catch (JsonProcessingException e) {
			log.error("ERR ORQUESTADOR-EXPERIAN - evento mal formado al recibirlo de Experian", e);
			throw new AgoraException(HttpStatus.BAD_REQUEST.value(), "evento mal formado", 
					Map.of("campo", "request", "valor", request));
		} catch  (DataIntegrityViolationException e) {
			// solicitud no existe
	    	log.error("ERR ORQUESTADOR-EXPERIAN -  solicitud no encontrada: " + request.getQueryId());
	        throw new AgoraException(HttpStatus.NOT_FOUND.value(), "solicitud no encontrada o notificacion duplicada", 
	        		Map.of("campo", "queryId", "mensaje", request.getQueryId()));

		}

    }
	
	
	/**
	 * Cuando empieza el ciclo de vida del worker reclama de la cola un evento pte de procesar
	 * @return
	 */
	public Optional<EventoExperianVivoEntity> reclamarEvento() {
        return eventoExperianWorkerRepository.reclamarEvento();
    }
	
	
	/**
	 * Un evento en que se interrumpido por error Tecnico (cae bdd etc) se reprograma para ejecutarlo mas tarde
	 * @param eventoId
	 * @param error
	 * @param errorMesg
	 */
    public void reprogramarEvento(Long eventoId, String error, String errorMesg) {
        eventoExperianWorkerRepository.reprogramarEvento(eventoId, error, errorMesg);
    }
    
    /**
     * un evento procesado de borra de la cola del worker y se pasa al historico
     * @param evento
     * @param dto
     */
    public void finalizarEventoTecnico(EventoExperianVivoEntity evento,
                                       EventoProcesadoDto dto) {

        eventoExperianWorkerRepository.finalizarEvento(evento, dto);
    }
    
    /**
	 * Si un evento que el worker no lo puede terminar de finalizar por error funcional
	 * lo pasa a la tabla de errores
	 * lo borrar de la cola de eventos a procesar
	 * Ej: evento ya procesado y que este en la tabla historicos
	 * @param evento
	 * @param eventoProcesadoDto
	 */
    public void eventoConErrorFuncional (EventoExperianVivoEntity evento, EventoProcesadoDto eventoProcesadoDto) {
		//guardar en tabla de errores
		EventoExperianErrorEntity errorEntity = new EventoExperianErrorEntity(
				evento.getId(),
				evento.getQueryId(),
				evento.getNotificationId(),
				evento.getOrigenEvento(),
				evento.getEventType(),
				eventoProcesadoDto.getEstadoExperian(),
				eventoProcesadoDto.getSubestadoExperian(),
				eventoProcesadoDto.getDocumento().getDocumentCode(),
				evento.getPayloadJson(),
				evento.getErrorCode(),
				evento.getErrorMensaje(),
				OffsetDateTime.now(ZoneOffset.UTC)
			);
		eventoExperianErrorRepository.save(errorEntity);
		
		//borrar de tabla viva
		eventoExperianVivoRepository.deleteById(evento.getId());
		
	}
    
    /** borrar un evento de la cola de trabajo del worker
     * 
     * @param idEvento
     */
    public void borrarDeVivo(Long idEvento) {
        eventoExperianVivoRepository.deleteById(idEvento);
    }

}
