package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.EVENT_STATUS_CHANGED;
import static bs.experian.orquestador.domain.constants.ExperianConstants.STATUS_SUCCESS;
import static bs.experian.orquestador.domain.constants.ExperianConstants.SUBSTATUS_ALL_DOCUMENTS_DOWNLOADED;

import org.springframework.stereotype.Component;

import bs.experian.orquestador.application.model.evento.EventoDto;

@Component
public class ProcadorAllParticalDocumentsDownloaded implements EventoProcesador {

	@Override
	public boolean aplica(EventoDto evento) {
		
		return EVENT_STATUS_CHANGED.equals(evento.getEventType())
	            && STATUS_SUCCESS.equals(evento.getEventData().getStatus())
	            && (SUBSTATUS_ALL_DOCUMENTS_DOWNLOADED.equals(evento.getEventData().getSubstatus())
	                || SUBSTATUS_ALL_DOCUMENTS_DOWNLOADED.equals(evento.getEventData().getSubstatus()));
	}

	@Override
	public void procesar(EventoDto evento) {


	}

}
