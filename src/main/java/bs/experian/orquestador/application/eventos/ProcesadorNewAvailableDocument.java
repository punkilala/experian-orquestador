package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.EVENT_NEW_DOCUMENT_AVAILABLE;
import static bs.experian.orquestador.domain.constants.ExperianConstants.STATUS_PROCESSING;
import static bs.experian.orquestador.domain.constants.ExperianConstants.SUBSTATUS_NEW_DOCUMENT_AVAILABLE;

import org.springframework.stereotype.Component;

import bs.experian.orquestador.application.DocumentosApplicagtionService;
import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import lombok.RequiredArgsConstructor;

/**
 * Experian comunica que un documento ya esta disponible
 * Se guarda en la tabla de procesos de documentos
 * Se envia a integración para que se descarge
 */
@Component
@RequiredArgsConstructor
public class ProcesadorNewAvailableDocument implements EventoProcesador {
	
	private final DocumentosApplicagtionService documentosApplicagtionService;

	@Override
	public boolean aplica(EventoProcesadoDto evento) {
		
		return EVENT_NEW_DOCUMENT_AVAILABLE.equals(evento.getEventType())
	            && STATUS_PROCESSING.equals(evento.getEstadoExperian())
	            && (SUBSTATUS_NEW_DOCUMENT_AVAILABLE.equals(evento.getSubestadoExperian()));
	}

	@Override
	public void procesar(EventoProcesadoDto evento) {
		//registrar documento en la tabla DocumentosSolicitdes y llamar a integracion pdara pasarselo
		documentosApplicagtionService.registrarDocumentoPteDescarga(evento);
		evento.setProcesado(true);
	}

}
