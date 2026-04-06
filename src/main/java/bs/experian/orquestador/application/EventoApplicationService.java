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
	 * Finalizar solicitud
	 * @param evento
	 * @param result
	 */
	@Transactional
	public void finalizarSolicitud (EventoDto evento, String result) {
		procesadorEventoJPARepository.moverEventoProcesadoToHist(evento, result, null, null );

		procesadorDocumentoRepository.moverDocumentosAHistorico(evento.getQueryId());
		
		procesadorSolicitudesRepository.actualizarDirectoEstadoSolicitud(
				evento.getQueryId(), 
				evento.getEventData().getStatus(), 
				evento.getEventData().getSubstatus(),
				evento.getEventData().getEstadoInternoFinal());
	}
}
