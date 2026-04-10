package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.DOC_PTE_CUSTODIA;
import static bs.experian.orquestador.domain.constants.ExperianConstants.EVENT_DOCUMENTO_DESCARGADO;
import static bs.experian.orquestador.domain.constants.ExperianConstants.STATUS_DOCUMENTO_DESCARGADO;
import static bs.experian.orquestador.domain.constants.ExperianConstants.STATUS_SUCCESS;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import bs.experian.orquestador.application.DocumentoApplicationService;
import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.domain.enums.DomainEnum;
import bs.experian.orquestador.infrastructure.dto.integracion.TopicKafkaDocumento;
import bs.experian.orquestador.infrastructure.kafka.produces.KafkaProduceOrdenDocumento;
import bs.experian.orquestador.infrastructure.persistence.documentos.DocumentosSolicitudBaseEntity;
import bs.experian.orquestador.infrastructure.persistence.documentos.DocumentosSolicitudEntity;
import bs.experian.orquestador.infrastructure.persistence.documentos.DocumentosSolicitudHistEntity;
import bs.experian.orquestador.infrastructure.persistence.documentos.ProcesadorDocumentoRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcesadorDocumentoDescargado implements EventoProcesador {
		
	private final ProcesadorDocumentoRepository procesadorDocumentoRepository;
	private final KafkaProduceOrdenDocumento kafkaProduceOrdenDocumento;
	private final ProcesadorAllPartialDocumentsDownloaded procesadorAllPartialDocumentsDownloaded;
	private final DocumentoApplicationService documentoApplicationService;
	
	@Override
	public boolean aplica(EventoDto evento) {
		return EVENT_DOCUMENTO_DESCARGADO.equals(evento.getEventType())
				&& evento.getEventData() != null
	            && STATUS_DOCUMENTO_DESCARGADO.equals(evento.getEventData().getStatus());
	}

	@Override
	public void procesar(EventoDto evento) {
		evento.getEventData().setOrigen("INTEGRACION");
		//actualizar bdd estado documento
		procesadorDocumentoRepository.actualizarResultDocumentoSolicitud(evento);
		//PUBLICA TOPIC PTE_CUSTODIA
		if(DOC_PTE_CUSTODIA.equals(evento.getEventData().getPdfEstado())) {
			TopicKafkaDocumento mensaje = new TopicKafkaDocumento();
			mensaje.setQueryId(evento.getQueryId());
			mensaje.setNotificationId(evento.getNotificationId());
			mensaje.setDocumentCode(evento.getEventData().getDocumentCode());
			
			kafkaProduceOrdenDocumento.publicar(mensaje, "documento.custodia.orden");
		}else {
			//documentos tardios que no se han podido descargar despues de haber recibido all o partical_documents_donwloaded
			if(STATUS_SUCCESS.equals(evento.getEventData().getSolicitudActual().getEstadoExperian())){
				recalcularEstado(evento);
			}
		}
	}
	
	private void recalcularEstado (EventoDto evento) {
		List<DocumentosSolicitudEntity> docs = documentoApplicationService.listatDocumentosTablaActiva(evento.getQueryId());
		List<DocumentosSolicitudBaseEntity> docsBase = new ArrayList<>(docs);
		
		if(procesadorAllPartialDocumentsDownloaded.hayDocumentosPteProceso(docsBase)) {
			return;
		}
		
		List<DocumentosSolicitudHistEntity> docHist = documentoApplicationService.listaDocumentosHistorico(evento.getQueryId());
		docsBase.addAll(docHist);
		
		DomainEnum.EstadoInterno result = procesadorAllPartialDocumentsDownloaded.calcularEstadoSolicitudPorEstadoDocumento(docsBase);
		
		evento.getEventData().getSolicitudActual().setEstadoInterno(result);
		evento.getEventData().setEventoFinal(true);
	}

}
