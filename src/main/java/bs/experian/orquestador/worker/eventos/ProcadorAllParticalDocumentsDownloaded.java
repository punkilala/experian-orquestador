package bs.experian.orquestador.worker.eventos;

import bs.experian.orquestador.dto.EventoProcesadoDto;
import static bs.experian.orquestador.constants.ExperianConstants.*;

import org.springframework.stereotype.Component;

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
	public EventoProcesadoDto procesar(EventoProcesadoDto evento) {
		// TODO Auto-generated method stub
		return null;
	}

}
