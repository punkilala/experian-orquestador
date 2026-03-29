package bs.experian.orquestador.infrastructure.persistence.documentos;

import java.time.OffsetDateTime;
import java.util.Objects;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.infrastructure.exceptions.NonRetryableProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static bs.experian.orquestador.domain.constants.ExperianConstants.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProcesadorDocumentoRepository {
	
	private final DocumentosSolicitudHistRepository documentosHistSolicitudesRepository;
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
	                    .orElseGet(() -> DocumentosSolicitudEntity.builder()
	                            .queryId(evento.getQueryId())
	                            .fechaAlta(OffsetDateTime.now())
	                            .build());
	    
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
	    	throw new NonRetryableProcessingException("DOCUMENTO DUPLICADO", null );
	    }
	}
	
	/**
	 * Actualizar estado documenton despues del proceso de descarga y custodia
	 * @param evento
	 */
	@Transactional
	public void actualizarResultDocumentoSolicitud (EventoDto evento) {
		
		//obtener el documento de la tabla de solicitudes
		DocumentosSolicitudEntity entity = documentosSolicitudesRespository.findById(
					new DocumentosSolicitudPK(evento.getQueryId(), evento.getEventData().getDocumentCode()))
				.orElseThrow(()->new  NonRetryableProcessingException("No se encuentra documento descargado en tabla SOLICITUDES", null));
		
		//para descarga y custodia doucmentos
		entity.setFechaUltimaActualizacion(OffsetDateTime.now());
		
		if("DocumentoDescargado".equals(evento.getEventType())) {
			entity.setEstadoDocumento(evento.getEventData().getSubstatus());
			entity.setDocumentPdf(evento.getEventData().getPdfEstado());
		}else {
			entity.setDocumentPdf(evento.getEventData().getSubstatus());
		}
		
		if ("DESCARGADO".equals(evento.getEventData().getJsonEstado())){
			//tabla documentos temporales
			DocumentoPendienteCustodiaEntity entityTpm = documentoPendienteCustodiaRepository.findById(
						new DocumentoPendienteCustodiaPK(evento.getQueryId(), evento.getEventData().getDocumentCode()))
					.orElseThrow(()->new  NonRetryableProcessingException("No se encuentra documento descargado en tabla TEMPORAL", null));
			
			entity.setDocumentJson(entityTpm.getJsonDocument());
		}
		
		documentosSolicitudesRespository.save(entity);
		
		if( "NO_DESCARGADO".equals(evento.getEventData().getPdfEstado()) || "CustodiaDocumento".equals(evento.getEventType())) {
			documentoPendienteCustodiaRepository.deleteById(
					new DocumentoPendienteCustodiaPK(evento.getQueryId(), evento.getEventData().getDocumentCode())
			);
		}
	}	
}
