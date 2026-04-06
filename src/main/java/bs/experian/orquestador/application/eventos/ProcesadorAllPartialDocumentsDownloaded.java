package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.*;

import java.util.List;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;


import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.infrastructure.persistence.documentos.DocumentosSolicitudEntity;
import bs.experian.orquestador.infrastructure.persistence.documentos.ProcesadorDocumentoRepository;
import lombok.RequiredArgsConstructor;
import static bs.experian.orquestador.domain.constants.ExperianConstants.*;

/**
 * Experian comunica estado final todos/parcial documentos descargados
 */
@Component
@RequiredArgsConstructor
public class ProcesadorAllPartialDocumentsDownloaded implements EventoProcesador {
	
	private final ProcesadorDocumentoRepository procesadorDocumentoRepository;


	@Override
	public boolean aplica(EventoDto evento) {
		
		return EVENT_STATUS_CHANGED.equals(evento.getEventType())
	            && STATUS_SUCCESS.equals(evento.getEventData().getStatus())
	            && (
	                SUBSTATUS_ALL_DOCUMENTS_DOWNLOADED.equals(evento.getEventData().getSubstatus())
	                || SUBSTATUS_PARTIAL_DOCUMENTS_DOWNLOADED.equals(evento.getEventData().getSubstatus())
	            );
	}

	@Override
	public void procesar(EventoDto evento) {
		List<DocumentosSolicitudEntity> docs = procesadorDocumentoRepository.obtenerDocumentosSolicitud(evento.getQueryId());
		
		boolean hayPendientes = docs.stream().anyMatch(doc ->
	        DOC_PTE_DESCARGA.equals(doc.getDocumentPdf())
	        || DOC_PTE_CUSTODIA.equals(doc.getDocumentPdf()));
		
		if(hayPendientes) {
			return;
		}
		
		String result = "";
		
		boolean hayKo = docs.stream().anyMatch(doc->
			DOC_CUSTODIA_KO.equals(doc.getDocumentPdf()));
		boolean hayOk = docs.stream().anyMatch(doc->
		DOC_CUSTODIA_OK.equals(doc.getDocumentPdf()));
		
		if (hayKo && hayOk) {
	        result = CUSTODIA_PARTICAL;
	    } else if (hayKo) {
	       result  = ERROR_CUSTODIA;
	    } else if (hayOk) {
	       result = CUSTODIA_COMPLETA;
	    }
		
		evento.getEventData().setEventoFinal(true);
		evento.getEventData().setEstadoInternoFinal(result);
		
		
	}

}
