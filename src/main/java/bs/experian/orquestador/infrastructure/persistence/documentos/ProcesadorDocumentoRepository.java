package bs.experian.orquestador.infrastructure.persistence.documentos;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Repository;

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
	
}
