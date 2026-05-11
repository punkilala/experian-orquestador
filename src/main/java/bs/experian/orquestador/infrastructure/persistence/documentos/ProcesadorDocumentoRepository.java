package bs.experian.orquestador.infrastructure.persistence.documentos;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.infrastructure.exceptions.NonRetryableProcessingException;
import lombok.RequiredArgsConstructor;
import static bs.experian.orquestador.domain.constants.ExperianConstants.*;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProcesadorDocumentoRepository {
	
	private final DocumentosSolicitudHistRepository documentosSolicitudHistRepository;
	private final DocumentosSolicitudRespository documentosSolicitudesRespository;
	private final DocumentoPendienteCustodiaRepository documentoPendienteCustodiaRepository;
	
	
	/**
	 * Registrar documento como pendiente descargar
	 * @param dto
	 */
	public void registrarDocumentoPteDescarga (EventoDto evento) {
		DocumentosSolicitudPK pk = new DocumentosSolicitudPK(evento.getQueryId(), evento.getEventData().getDocumentCode());
	    DocumentosSolicitudEntity entity =
	            documentosSolicitudesRespository.findById(pk)
	                    .orElseGet(() -> {
		                    DocumentosSolicitudEntity entityNew = new DocumentosSolicitudEntity();
		                    entityNew.setQueryId(evento.getQueryId());
		                    entityNew.setFechaAlta(OffsetDateTime.now());
		                    return entityNew;
	                    });
	    
	    // si es la primera vez que se trata el documento de una solicitud
	    if (!Objects.equals(evento.getEventData().getDocumentCode(), entity.getDocumentCode())) {

	        entity.setNotificationId(evento.getNotificationId());
	        entity.setDocumentCode(evento.getEventData().getDocumentCode());
	        entity.setDocumentJson(null);
	        entity.setDocumentPdf(null);
	        entity.setEstadoDocumento(DOC_PTE_DESCARGA);

	        documentosSolicitudesRespository.save(entity);
	    }else {
	    	//documento de la solicitud ya tratado
	    	throw new NonRetryableProcessingException("DOCUMENTO DUPLICADO", "Documento %s ya procesado para solicitud %s"
	    			.formatted(evento.getEventData().getDocumentCode(), evento.getQueryId()));
	    }
	}
	
	/**
	 * Actualizar estado documenton despues del proceso de descarga y custodia
	 * @param evento
	 */
	public void actualizarResultDocumentoSolicitud (EventoDto evento) {
		
		//obtener el documento de la tabla de solicitudes
		DocumentosSolicitudEntity entity = documentosSolicitudesRespository.findById(
					new DocumentosSolicitudPK(evento.getQueryId(), evento.getEventData().getDocumentCode()))
				.orElseThrow(()->new  NonRetryableProcessingException("DOCUMENTO NO EXISTE ", "No se encuentra documento %s descargado para solicitud %s"
						.formatted(evento.getEventData().getDocumentCode(), evento.getQueryId())));
		
		//para descarga y custodia doucmentos
		entity.setFechaUltimaAct(OffsetDateTime.now());
		entity.setEstadoDocumento(evento.getEventData().getResultDescargaCustodia());
		entity.setDocumentPdf(evento.getEventData().getPdfEstado());
		entity.setDocumentJson(evento.getEventData().getJsonEstado());
		
		
		documentosSolicitudesRespository.save(entity);
		
		
	}
	/**
	 * Obtener todos los documentos de la solicitud activa
	 * @param queryId
	 */
	public List<DocumentosSolicitudEntity>  obtenerDocumentosSolicitud (String queryId) {
		
		return documentosSolicitudesRespository.findByQueryId(queryId);
	}
	
	/**
	 * obtener todos los documentos hist de la solicitud
	 * @param queryId
	 * @return
	 */
	public List<DocumentosSolicitudHistEntity> obtenerDocumentosHistSolicitud (String queryId){
		return documentosSolicitudHistRepository.findByQueryId(queryId);
	}
	
//	public void moverDocumentosAHistorico(String queryId) {
//	    List<DocumentosSolicitudEntity> documentos = documentosSolicitudesRespository.findByQueryId(queryId);
//
//	    if (documentos.isEmpty()) {
//	    	//si no hay documentos que mover a hist, no hacer nada
//	        return;
//	    }
//
//	    List<DocumentosSolicitudHistEntity> historicos = documentos.stream()
//	            .map(this::toHistorico)
//	            .toList();
//
//	    documentosSolicitudHistRepository.saveAll(historicos);
//	    documentosSolicitudesRespository.deleteAllInBatch(documentos);
//	}
//	private DocumentosSolicitudHistEntity toHistorico(DocumentosSolicitudEntity doc) {
//	    DocumentosSolicitudHistEntity hist = new DocumentosSolicitudHistEntity();
//
//	    hist.setQueryId(doc.getQueryId());
//	    hist.setDocumentCode(doc.getDocumentCode());
//	    hist.setNotificationId(doc.getNotificationId());
//	    hist.setEstadoDocumento(doc.getEstadoDocumento());
//	    hist.setDocumentJson(doc.getDocumentJson());
//	    hist.setDocumentPdf(doc.getDocumentPdf());
//	    hist.setFechaAlta(doc.getFechaAlta());
//	    hist.setFechaUltimaAct(doc.getFechaUltimaAct());
//	    hist.setFechaCierre(OffsetDateTime.now());
//
//	    return hist;
//	}
}
