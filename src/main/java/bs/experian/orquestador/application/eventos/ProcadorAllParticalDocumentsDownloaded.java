package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.*;

import org.springframework.stereotype.Component;

import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;

@Component
public class ProcadorAllParticalDocumentsDownloaded implements EventoProcesador {

	@Override
	public boolean aplica(EventoProcesadoDto evento) {
		
		return EVENT_STATUS_CHANGED.equals(evento.getEventType())
	            && STATUS_SUCCESS.equals(evento.getEstadoExperian())
	            && (SUBSTATUS_ALL_DOCUMENTS_DOWNLOADED.equals(evento.getSubestadoExperian())
	                || SUBSTATUS_ALL_DOCUMENTS_DOWNLOADED.equals(evento.getSubestadoExperian()));
	}

	@Override
	public void procesar(EventoProcesadoDto evento) {
		// TODO Auto-generated method stub

	}

}
