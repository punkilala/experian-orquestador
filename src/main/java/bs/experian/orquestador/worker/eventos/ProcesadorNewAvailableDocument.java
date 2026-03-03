package bs.experian.orquestador.worker.eventos;

import static bs.experian.orquestador.constants.ExperianConstants.EVENT_NEW_DOCUMENT_AVAILABLE;
import static bs.experian.orquestador.constants.ExperianConstants.STATUS_PROCESSING;
import static bs.experian.orquestador.constants.ExperianConstants.SUBSTATUS_NEW_DOCUMENT_AVAILABLE;

import org.springframework.stereotype.Component;

import bs.experian.orquestador.dto.EventoProcesadoDto;
import bs.experian.orquestador.service.integracion.OrdenDescargaDocumentoService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcesadorNewAvailableDocument implements EventoProcesador {
	
	private final OrdenDescargaDocumentoService service;

	@Override
	public boolean aplica(EventoProcesadoDto evento) {
		
		return EVENT_NEW_DOCUMENT_AVAILABLE.equals(evento.getEventType())
	            && STATUS_PROCESSING.equals(evento.getEstadoExperian())
	            && (SUBSTATUS_NEW_DOCUMENT_AVAILABLE.equals(evento.getSubestadoExperian()));
	}

	@Override
	public EventoProcesadoDto procesar(EventoProcesadoDto evento) {
		
		service.ordenarDescarga(evento);
		evento.setProcesado(true);
		
		return evento;
	}

}
