package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import bs.experian.orquestador.application.DocumentoApplicationService;
import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.domain.enums.DomainEnum.EstadoInterno;
import bs.experian.orquestador.infrastructure.persistence.documentos.DocumentosSolicitudBaseEntity;
import bs.experian.orquestador.infrastructure.persistence.documentos.DocumentosSolicitudEntity;
import bs.experian.orquestador.infrastructure.persistence.documentos.DocumentosSolicitudHistEntity;
import bs.experian.orquestador.infrastructure.persistence.documentos.ProcesadorDocumentoRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcesadorCustodiaDocumento implements EventoProcesador {
	
	private final ProcesadorDocumentoRepository procesadorDocumentoRepository;
	private final DocumentoApplicationService documentoApplicationService;
	private final ProcesadorAllPartialDocumentsDownloaded procesadorAllPartialDocumentsDownloaded;
	
	@Override
	public boolean aplica(EventoDto evento) {
		return EVENT_TYPE_CUSTODIA.equals(evento.getEventType())
	            && STATUS_CUSTODIA.equals(evento.getEventData().getStatus());
	}

	@Override
	public void procesar(EventoDto evento)  {
		evento.getEventData().setOrigen("CUSTODIA");
		//actualizar bdd estado documento
		procesadorDocumentoRepository.actualizarResultDocumentoSolicitud(evento);
		
		//recepcion documento tardio una vez que Experian ya notifico all o partial_documents_downloaded
		if(STATUS_SUCCESS.equals(evento.getEventData().getSolicitudActual().getEstadoExperian())) {
			List<DocumentosSolicitudEntity> docs = documentoApplicationService.listatDocumentosTablaActiva(evento.getQueryId());
			List<DocumentosSolicitudBaseEntity> docsBase = new ArrayList<>(docs);
			
			if(procesadorAllPartialDocumentsDownloaded.hayDocumentosPteProceso(docsBase)) {
				return;
			}
			
			List<DocumentosSolicitudHistEntity> docHist = documentoApplicationService.listaDocumentosHistorico(evento.getQueryId());
			docsBase.addAll(docHist);
			
			EstadoInterno result = procesadorAllPartialDocumentsDownloaded.calcularEstadoSolicitudPorEstadoDocumento(docsBase);

		    evento.getEventData().getSolicitudActual().setEstadoInterno(result);
			evento.getEventData().setEventoFinal(true);
		}
		
	}
}



