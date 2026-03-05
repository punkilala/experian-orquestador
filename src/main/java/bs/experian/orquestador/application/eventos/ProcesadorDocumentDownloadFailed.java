package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.EVENT_STATUS_CHANGED;
import static bs.experian.orquestador.domain.constants.ExperianConstants.STATUS_ERROR;
import static bs.experian.orquestador.domain.constants.ExperianConstants.SUBSTATUS_DOCUMENT_DWONLOAD_FAILED;

import org.springframework.stereotype.Component;

import bs.experian.orquestador.application.OrquestadorTxService;
import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
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
	public void procesar(EventoProcesadoDto evento) {
		txService.docErrorExperianAHit(evento);
		evento.setProcesado(true);
	}

}
