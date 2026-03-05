package bs.experian.orquestador.application;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import bs.experian.orquestador.domain.services.DocumentoService;
import bs.experian.orquestador.domain.services.EventoService;
import bs.experian.orquestador.domain.services.SolicitudService;
import bs.experian.orquestador.infrastructure.dto.integracion.SolicitudNuevaRequest;
import bs.experian.orquestador.infrastructure.dto.orquestador.SolicitudNuevaResponse;
import bs.experian.orquestador.infrastructure.persistence.eventos.EventoExperianVivoEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrquestadorTxService {
	
	private final SolicitudService solicitudService;
	private final EventoService eventoService;
	private final DocumentoService documentoService;
	
	
	/**
	 * alta nueva solicitud experian
	 * @param request
	 * @param response
	 */
	public void crearSolicitud(SolicitudNuevaRequest request, SolicitudNuevaResponse response) {
	    solicitudService.crearSolicitud(request, response);
	}
	
	/**
	 * recuperar un evento de la cola para procesar
	 * @return
	 */
	public Optional<EventoExperianVivoEntity> reclamarEvento() {
	    return eventoService.reclamarEvento();
	}
	
	/**
	 * reprogramar un evento por error tecnico
	 * @param evendoId
	 * @param error
	 * @param errorMesg
	 */
	public void reprogramarEvento(Long eventoId, String error, String errorMesg) {
	    eventoService.reprogramarEvento(eventoId, error, errorMesg);
	}
	
	
	/**
	 * finalizar el procesamiento de un evento
	 * @param evento
	 * @param eventoProcesadoDto
	 */
	@Transactional
	public void finalizarEvento (EventoExperianVivoEntity evento, EventoProcesadoDto eventoProcesadoDto) {
		
		//actualizar evento
		eventoService.finalizarEventoTecnico(evento, eventoProcesadoDto);
		
		//actualizar solicitud
	    solicitudService.actualizarDesdeEvento(evento.getQueryId(), eventoProcesadoDto);
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
	public void eventoConErrorFuncional(EventoExperianVivoEntity evento, EventoProcesadoDto dto) {
	    eventoService.eventoConErrorFuncional(evento, dto);
	}
	
	
	/**
	 * si se produces un evento  DOCUMENT_DWONLOAD_FAILED es un fallo de que experian no ha podido obtener el documento
	 * El evento se pasa a historisco de documentos para trazabilidad
	 * @param evento
	 */
	@Transactional
	public void docErrorExperianAHit (EventoProcesadoDto evento) {
		//pasar evento a historico de documentos
		documentoService.registrarExperianKOEnHistorico(evento);
		//borrar de tabla de trabajo
	    eventoService.borrarDeVivo(evento.getIdLong());		
	}

}
