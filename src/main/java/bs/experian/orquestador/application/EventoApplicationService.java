package bs.experian.orquestador.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.infrastructure.persistence.documentos.ProcesadorDocumentoRepository;
import bs.experian.orquestador.infrastructure.persistence.eventos.ProcesadorEventoJPARepository;
import bs.experian.orquestador.infrastructure.persistence.solicitud.ProcesadorSolicitudesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static bs.experian.orquestador.domain.constants.ExperianConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventoApplicationService {
	private final ProcesadorEventoJPARepository procesadorEventoJPARepository;
	private final ProcesadorSolicitudesRepository procesadorSolicitudesRepository;
	private final ProcesadorDocumentoRepository procesadorDocumentoRepository;


	/**
	 * finalizar el procesamiento de un evento
	 * @param evento
	 * @param eventoProcesadoDto
	 */
	@Transactional
	public void finalizarEvento (EventoDto evento, String result, String errCode, String errMsj) {
		procesadorEventoJPARepository.moverEventoProcesadoToHist(evento, result, errCode, errMsj );
			
		//actualizar solicitud
		if(null == errCode && null ==  errMsj) {
			procesadorSolicitudesRepository.actualizarEstadoSolicitud(evento);
		}
	}
	
	/**
	 * finalizar el procesamiento de un evento  cuando se registra un nuevo documento
	 * @param evento
	 * @param result
	 * @param errCode
	 * @param errMsj
	 */
	@Transactional
	public void finalizarEventoConDocumento (EventoDto evento) {
		//registrar nuevo documento
		if(evento.getEventType().equals(EVENT_NEW_DOCUMENT_AVAILABLE)) {
			procesadorDocumentoRepository.registrarDocumentoPteDescarga(evento);
		} else if (evento.getEventType().equals(EVENT_RESULT_DESCARGA_CUSTODIA_DOCUMENTO)) {
			//actualizar resultado descarga y custodia
			procesadorDocumentoRepository.actualizarResultDocumentoSolicitud(evento);
		}
		
		//evento a historico
		procesadorEventoJPARepository.moverEventoProcesadoToHist(evento, "PROCESADO", null, null );
		
		//actualizar solicitud
		procesadorSolicitudesRepository.actualizarEstadoSolicitud(evento);

	}
	
	/**
	 * Finalizar solicitud
	 * @param evento
	 * @param result
	 */
	@Transactional
	public void finalizarSolicitud (EventoDto evento, String result) {
		if(evento.getEventType().equals(EVENT_RESULT_DESCARGA_CUSTODIA_DOCUMENTO)) {
			procesadorDocumentoRepository.actualizarResultDocumentoSolicitud(evento);
		}
		procesadorEventoJPARepository.moverEventoProcesadoToHist(evento, result, null, null );
		
		procesadorSolicitudesRepository.actualizarEstadoFinalSolicitud(evento);
	}
}
