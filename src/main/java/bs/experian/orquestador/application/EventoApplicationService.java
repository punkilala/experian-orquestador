package bs.experian.orquestador.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.infrastructure.persistence.eventos.ProcesadorEventoJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class EventoApplicationService {
	
	private final SolicitudApplicationService solicitudApplicationService;
	private final ProcesadorEventoJPARepository procesadorEventoJPARepository;


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
			solicitudApplicationService.actualizarEstadoSolicitud(evento);
		}
	}
}
