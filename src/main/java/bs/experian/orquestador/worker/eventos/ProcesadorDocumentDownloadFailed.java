package bs.experian.orquestador.worker.eventos;

import static bs.experian.orquestador.constants.ExperianConstants.EVENT_STATUS_CHANGED;
import static bs.experian.orquestador.constants.ExperianConstants.STATUS_ERROR;
import static bs.experian.orquestador.constants.ExperianConstants.SUBSTATUS_DOCUMENT_DWONLOAD_FAILED;

import org.springframework.stereotype.Component;

import bs.experian.orquestador.dto.EventoProcesadoDto;
import bs.experian.orquestador.service.aplicacion.OrquestadorTxService;
import lombok.RequiredArgsConstructor;

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
	public EventoProcesadoDto procesar(EventoProcesadoDto evento) {
		txService.docErrorExperianAHit(evento);
		evento.setProcesado(true);
		return null;
	}

}
