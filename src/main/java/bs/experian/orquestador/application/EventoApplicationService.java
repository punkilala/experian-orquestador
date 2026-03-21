package bs.experian.orquestador.application;

import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import static bs.experian.orquestador.application.utils.OrquestadorUtils.*;
import bs.experian.orquestador.infrastructure.exceptions.AgoraException;
import bs.experian.orquestador.infrastructure.persistence.documentos.ProcesadorDocumentoRepository;
import bs.experian.orquestador.infrastructure.persistence.eventos.ProcesadorEventoJDBCRepository;
import bs.experian.orquestador.infrastructure.persistence.eventos.ProcesadorEventoJPARepository;
import bs.experian.orquestador.infrastructure.persistence.eventos.entity.EventoExperianVivoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static bs.experian.orquestador.domain.constants.ExperianConstants.*;
import static bs.experian.orquestador.domain.enums.DomainEnum.TiposEventosHistoricos.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class EventoApplicationService {
	private final ObjectMapper objectMapper;
	
	private final SolicitudApplicationService solicitudApplicationService;
	
	private final ProcesadorEventoJPARepository procesadorEventoJPARepository;
	private final ProcesadorEventoJDBCRepository procesadorEventoJDBCRepository;
	private final ProcesadorDocumentoRepository procesadorDocumentoRepository;

	
	/**
	 * Evento recibido de experian lo guardamos en la cola de trabajo del worker
	 * @param request
	 */
	public void recibirEventoExperian(EventoDto request) {
		String payload = "";
		String origen = null;
		try {
			payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
			
			origen = null != request.getOrigen() ? request.getOrigen() : EXPERIAN.name();
			
			if(EVENT_NEW_DOCUMENT_AVAILABLE.equalsIgnoreCase(request.getEventType())) {
				procesadorDocumentoRepository.buscarNotificacionDuplicadaEnDoc(request.getQueryId(), request.getNotificationId());
			}
			
			//encolar evento recibido
			procesadorEventoJPARepository.encolarEvento(request, origen, payload);
			
		} catch (JsonProcessingException e) {
			log.error("ERR ORQUESTADOR-EXPERIAN - evento mal formado al recibirlo de Experian", e);
			throw new AgoraException(HttpStatus.BAD_REQUEST.value(), "evento mal formado", 
					Map.of("origen", "request", "valor", request));
			
		} catch  (DataIntegrityViolationException e) {
			// solicitud no existe
			String errOra = getOra(e);
			if(ORA_NO_EXISTE.equalsIgnoreCase(errOra)) {
				log.error("ERR ORQUESTADOR-EXPERIAN -  solicitud no encontrada: " + request.getQueryId());
		        throw new AgoraException(HttpStatus.NOT_FOUND.value(), "solicitud no encontrada", 
		        		Map.of("campo", "queryId", "mensaje", request.getQueryId()));
			}else if ("DUPLICADO".equals(e.getMessage())){
				log.error("ERR ORQUESTADOR-EXPERIAN -  Notificacion duplicada para: " + request.getQueryId());
				procesadorEventoJPARepository.moverEventoNoProcesadoToHist(request, origen, payload, "NOK", e.getMessage(), stackTraceToString(e, 20));
		       
			}else {
				log.error("ERR ORQUESTADOR-EXPERIAN -  error de BDD para: " + request.getQueryId(), stackTraceToString(e, 20));
		        throw new AgoraException(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error en bdd", 
		        		Map.of("campo", "Bdd", "mensaje", request.getQueryId()));
			}

		}
	}
	
	/**
	 * Cuando empieza el ciclo de vida del worker reclama de la cola un evento pte de procesar
	 * @return
	 */
	@Transactional
	public Optional<EventoExperianVivoEntity> reclamarEvento() {
        return procesadorEventoJDBCRepository.reclamarEvento();
    }
	
	
	/**
	 * un evento que ha fallado su procesamiento por error tecnico, timeout, conexiones caidas se reprograma
	 * para una ejecucion dentro de una hora
	 * @param eventoId
	 * @param error
	 * @param errorMesg
	 */
	public void reprogramarEvento(Long eventoId, String error, String errorMesg) {
		procesadorEventoJPARepository.reprogramarEvento(eventoId, error, errorMesg);
    }
	
	/**
	 * finalizar el procesamiento de un evento
	 * @param evento
	 * @param eventoProcesadoDto
	 */
	@Transactional
	public void finalizarEvento (EventoExperianVivoEntity evento, EventoProcesadoDto eventoProcesadoDto) {
		
		//mover evento a historico
		procesadorEventoJPARepository.moverEventoProcesadoToHist(evento, eventoProcesadoDto, "OK");
		
		//borrar evento de la cola de trabajo del worker
		procesadorEventoJPARepository.borraEventoDeColaWorker(evento.getId());
		
		//actualizar solicitud
	    solicitudApplicationService.actualizarEstadoSolicitud(evento.getQueryId(), eventoProcesadoDto);
	}
	
	/**
	 * Si un evento que el worker no lo puede terminar de finalizar por error funcional
	 * lo pasa a la tabla de errores
	 * lo borrar de la cola de eventos a procesar
	 * Ej: evento ya procesado y que este en la tabla historicos
	 * @param evento
	 * @param eventoProcesadoDto
	 */
	@Transactional
	public void eventoNoProcesadoErrorFuncional(EventoExperianVivoEntity evento, EventoProcesadoDto dto) {
		procesadorEventoJPARepository.eventoNoProcesadoErrorFuncional(evento, dto, "NOK");
	}
	

}
