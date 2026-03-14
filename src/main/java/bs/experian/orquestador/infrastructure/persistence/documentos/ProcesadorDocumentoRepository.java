package bs.experian.orquestador.infrastructure.persistence.documentos;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProcesadorDocumentoRepository {
	
	private final DocumentosSolicitudHistRepository documentosHistSolicitudesRepository;
	private final DocumentosSolicitudRespository documentosSolicitudesRespository;
	
	
	/**
	 * Registrar documento como pendiente descargar
	 * @param dto
	 */
	public void registrarDocumentoPteDescarga (EventoProcesadoDto dto) {

		DocumentosSolicitudEntity entity =
				DocumentosSolicitudEntity.builder()
					.queryId(dto.getQueryId())
					.documentCode(dto.getDocumento().getDocumentCode())
					.estadoDocumento("PTE_DESCARGA")
					.build();
		
		documentosSolicitudesRespository.save(entity);			
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
                        .estadoDocumento(dto.getSubestadoExperian())
                        .errorCode(dto.getDocumento().getErrorCode())
                        .errorMensaje(dto.getDocumento().getErrorMessage())
                        .fechaAlta(OffsetDateTime.now(ZoneOffset.UTC))
                        .build();

        documentosHistSolicitudesRepository.save(entity);
    }
	
	/**
	 * Actualizar el resultasdo de la descaergar y saber si es custodiable
	 * @param dto
	 */
	@Transactional
	public void docummentoDescargado (EventoProcesadoDto dto) {
		
		DocumentosSolicitudEntity entity = documentosSolicitudesRespository
				.findById( new DocumentosSolicitudPK(
					dto.getQueryId(),
					dto.getDocumento().getDocumentCode()))
				.orElseThrow(()->
					new IllegalArgumentException(
	                "documento %s no se encuentra en la tabla de documentos"
	                    .formatted(dto.getDocumento().getDocumentCode())
	            ));
		
		String custodiable = 
				"OK".equalsIgnoreCase(dto.getSubestadoExperian()) 
				&& Boolean.TRUE.equals(dto.getDocumento().getPdfDocument())
	            	? "PTE_CUSTODIA"
	                : "KO";
		
		entity.setDescargadoPdf(String.valueOf(dto.getDocumento().getPdfDocument()));
		entity.setDocumentJson(dto.getDocumento().getJsonDocument());
		entity.setEstadoDocumento(custodiable);
		entity.setFechaUltimaActualizacion(OffsetDateTime.now());
					
	}
	
}
