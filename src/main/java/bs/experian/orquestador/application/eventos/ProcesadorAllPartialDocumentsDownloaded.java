package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.*;
import static bs.experian.orquestador.domain.enums.DomainEnum.EstadoInterno.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import bs.experian.orquestador.application.DocumentoApplicationService;
import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.domain.enums.DomainEnum.EstadoInterno;
import bs.experian.orquestador.infrastructure.persistence.documentos.DocumentosSolicitudBaseEntity;
import bs.experian.orquestador.infrastructure.persistence.documentos.DocumentosSolicitudEntity;
import lombok.RequiredArgsConstructor;

/**
 * Experian comunica estado final todos/parcial documentos descargados
 */
@Component
@RequiredArgsConstructor
public class ProcesadorAllPartialDocumentsDownloaded implements EventoProcesador {
	
    private final DocumentoApplicationService documentoApplicationService;

	@Override
	public boolean aplica(EventoDto evento) {
		
		return EVENT_STATUS_CHANGED.equals(evento.getEventType())
	            && STATUS_SUCCESS.equals(evento.getEventData().getStatus())
	            && ESTADOS_FINALES_DOCUMENTACION.contains(evento.getEventData().getSubstatus());
	}

	@Override
	public void procesar(EventoDto evento) {
		List<DocumentosSolicitudEntity> docs = documentoApplicationService.listatDocumentosTablaActiva(evento.getQueryId());
		List<DocumentosSolicitudBaseEntity> docsBase = new ArrayList<>(docs);
		
		if(hayDocumentosPteProceso(docsBase)) {
			return;
		}
		
		EstadoInterno result = calcularEstadoSolicitudPorEstadoDocumento(docsBase);
		

		evento.getEventData().setEventoFinal(true);
		evento.getEventData().getSolicitudActual().setEstadoInterno(result);
		evento.getEventData().getSolicitudActual().setEstadoExperian(evento.getEventData().getStatus());
		evento.getEventData().getSolicitudActual().setSubEstadoExperian(evento.getEventData().getSubstatus());
	}
	
	public boolean hayDocumentosPteProceso (List<DocumentosSolicitudBaseEntity> docs) {
		return  docs.stream().anyMatch(doc ->
        DOC_PTE_DESCARGA.equals(doc.getDocumentPdf())
        || DOC_PTE_CUSTODIA.equals(doc.getDocumentPdf()));
	}
	
	public EstadoInterno calcularEstadoSolicitudPorEstadoDocumento(List<DocumentosSolicitudBaseEntity> docs) {
		EstadoInterno result = null;
		
		boolean hayKo = docs.stream().anyMatch(doc->
			!DOC_CUSTODIA_OK.equals(doc.getDocumentPdf()));
		boolean hayOk = docs.stream().anyMatch(doc->
			DOC_CUSTODIA_OK.equals(doc.getDocumentPdf()));
		
		if (hayKo && hayOk) {
	        result = CUSTODIA_PARCIAL;
	    } else if (hayKo) {
	       result  = ERROR_CUSTODIA;
	    } else if (hayOk) {
	       result = CUSTODIA_COMPLETA;
	    }
		
		return result;
	}

}
