package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.EVENT_STATUS_CHANGED;
import static bs.experian.orquestador.domain.constants.ExperianConstants.STATUS_ERROR;
import static bs.experian.orquestador.domain.constants.ExperianConstants.SUBSTATUS_DOCUMENT_DWONLOAD_FAILED;

import org.springframework.stereotype.Component;

import bs.experian.orquestador.application.OrquestadorTxService;
import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import lombok.RequiredArgsConstructor;

/**
 * Experian comunica que el documento no se ha podibo obtener
 * El documento pasa a historico porque no se va a procesar su descarga
 */
@Component
@RequiredArgsConstructor
public class ProcesadorDocumentDownloadFailed implements EventoProcesador {
	
	private final OrquestadorTxService txService;

	@Override
	public boolean aplica(EventoProcesadoDto evento) {
		
		return EVENT_STATUS_CHANGED.equals(evento.getEventType())
	            && STATUS_ERROR.equals(evento.getEstadoExperian())
	            && (SUBSTATUS_DOCUMENT_DWONLOAD_FAILED.equals(evento.getSubestadoExperian()));
	}

	@Override
	public void procesar(EventoProcesadoDto evento) {
		txService.registrarDocEnHistExperianNoObtenido(evento);
		evento.setProcesado(true);
	}

}
