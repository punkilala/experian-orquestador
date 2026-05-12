package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import bs.experian.orquestador.application.DocumentoApplicationService;
import bs.experian.orquestador.application.EventoApplicationService;
import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.domain.enums.DomainEnum;
import bs.experian.orquestador.infrastructure.persistence.documentos.DocumentosSolicitudBaseEntity;
import bs.experian.orquestador.infrastructure.persistence.documentos.DocumentosSolicitudEntity;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcesadorEventDescargaDocumentoResult implements EventoProcesador {
		
	private final EventoApplicationService eventoApplicationService;
	private final ProcesadorAllPartialDocumentsDownloaded procesadorAllPartialDocumentsDownloaded;
	private final DocumentoApplicationService documentoApplicationService;
	
	@Override
	public boolean aplica(EventoDto evento) {
		return EVENT_RESULT_DESCARGA_CUSTODIA_DOCUMENTO.equals(evento.getEventType());
	}

	@Override
	@Transactional
	public void procesar(EventoDto evento) {
		evento.getEventData().setOrigen("SYS-EXPERIAN");
	
		//recepcion de documento tardia una vez se recibio all o partial_document_donwloaded
		if(ESTADOS_FINALES_DOCUMENTACION.contains(evento.getEventData().getSolicitudActual().getSubEstadoExperian())){
			recalcularEstado(evento);
		}else {
			eventoApplicationService.finalizarEventoConDocumento(evento);
		}
		
	
	}
	
	private void recalcularEstado (EventoDto evento) {
		List<DocumentosSolicitudEntity> docs = documentoApplicationService.listatDocumentosTablaActiva(evento.getQueryId());
		
		//si es un documento tardio, dicho documento aun no esta actualizado el resultado de descarga/custodia en la bdd
		docs.stream()
		    .filter(d -> evento.getEventData().getDocumentCode().equals(d.getDocumentCode()))
		    .findFirst()
		    .ifPresent(doc -> {
		        doc.setEstadoDocumento(evento.getEventData().getResultDescargaCustodia());
		        doc.setDocumentJson(evento.getEventData().getJsonEstado());
		        doc.setDocumentPdf(evento.getEventData().getPdfEstado());
	    });
		
		if(procesadorAllPartialDocumentsDownloaded.hayDocumentosPteProceso(docs)) {
			return;
		}
		
		DomainEnum.EstadoInterno result = procesadorAllPartialDocumentsDownloaded.calcularEstadoSolicitudPorEstadoDocumento(docs);
		
		evento.getEventData().getSolicitudActual().setEstadoInterno(result);
		evento.getEventData().setEventoFinal(true);
	}

}
