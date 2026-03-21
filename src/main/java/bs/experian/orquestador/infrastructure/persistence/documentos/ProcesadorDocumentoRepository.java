package bs.experian.orquestador.infrastructure.persistence.documentos;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static bs.experian.orquestador.domain.constants.ExperianConstants.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProcesadorDocumentoRepository {
	
	private final DocumentosSolicitudHistRepository documentosHistSolicitudesRepository;
	private final DocumentosSolicitudRespository documentosSolicitudesRespository;
	
	
	/**
	 * Registrar documento como pendiente descargar
	 * @param dto
	 */
	public void registrarDocumentoPteDescarga (EventoProcesadoDto dto) {
		DocumentosSolicitudPK pk = new DocumentosSolicitudPK(dto.getQueryId(), dto.getDocumento().getDocumentCode());
	    DocumentosSolicitudEntity entity =
	            documentosSolicitudesRespository.findById(pk)
	                    .orElseGet(() -> DocumentosSolicitudEntity.builder()
	                            .queryId(dto.getQueryId())
	                            .documentCode(dto.getDocumento().getDocumentCode())
	                            .fechaAlta(OffsetDateTime.now())
	                            .build());
	    
	    // si no hay duplicados de notificationId
	    if (!Objects.equals(dto.getNotificationId(), entity.getNotificationId())) {

	        entity.setNotificationId(dto.getNotificationId());
	        entity.setDocumentJson(null);
	        entity.setDocumentPdf(null);
	        entity.setEstadoDocumento(DOC_PTE_DESCARGA);

	        documentosSolicitudesRespository.save(entity);
	    }else {
	    	//mismo queryId para el mismo documento con misma notificacionId
	    	throw new DataIntegrityViolationException("DUPLICADO");
	    }
	}
	
	/**
	 * Pasar a historico un documento que experian dice que no lo ha obtenido
	 * @param dto
	 */
	@Transactional
	public void registrarDocEnHistExperianNoObtenido(EventoProcesadoDto dto) {

        DocumentosSolicitudHistEntity entity =
                DocumentosSolicitudHistEntity.builder()
                        .queryId(dto.getQueryId())
                        .documentCode(dto.getDocumento().getDocumentCode())
                        .errorCode(dto.getDocumento().getErrorCode())
                        .errorMensaje(dto.getDocumento().getErrorMessage())
                        .fechaAlta(OffsetDateTime.now(ZoneOffset.UTC))
                        .fechaCierre(OffsetDateTime.now())
                        .build();

        documentosHistSolicitudesRepository.save(entity);
    }
	
	/**
	 * Actualizar estado documento por resultado de su descargar o su  custodia
	 * @param dto
	 */
	
	public void actualizarEstadoDocumento (EventoProcesadoDto dto) {
		
		Optional <DocumentosSolicitudEntity> entityOpt = documentosSolicitudesRespository.findById(
					new DocumentosSolicitudPK(dto.getQueryId(), dto.getDocumento().getDocumentCode()));
		
		if (entityOpt.isEmpty()) {
		    log.error("ERR OQUESTADOR-EXPERIAN Documento no encontrado en solicitudes. queryId={}, documentCode={}",
		        dto.getQueryId(),
		        dto.getDocumento().getDocumentCode()
		    );
		    return;
		}
		
		DocumentosSolicitudEntity entity = entityOpt.get();

		if("DocumentoDescargado".equals(dto.getEventType())) {
			entity.setEstadoDocumento(dto.getSubestadoExperian());
			entity.setDocumentJson(dto.getDocumento().getJsonDocument());
		}
	    entity.setFechaUltimaActualizacion(OffsetDateTime.now());	
    	entity.setDocumentPdf(dto.getDocumento().getPdfDocument());
	    
	   	documentosSolicitudesRespository.save(entity);
					
	}
	
	public void buscarNotificacionDuplicadaEnDoc(String queryId, String notificationId) {
		
		if(documentosSolicitudesRespository.existsByQueryIdAndNotificationId(queryId, notificationId)) {
			throw new DataIntegrityViolationException("DUPLICADO");
		}
	}
	
}
