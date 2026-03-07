package bs.experian.orquestador.domain.services;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import bs.experian.orquestador.infrastructure.persistence.documentos.DocumentosSolicitudHistEntity;
import bs.experian.orquestador.infrastructure.persistence.documentos.DocumentosSolicitudHistRepository;
import bs.experian.orquestador.infrastructure.persistence.documentos.DocumentosSolicitudEntity;
import bs.experian.orquestador.infrastructure.persistence.documentos.DocumentosSolicitudRespository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentoService {
	
	private final DocumentosSolicitudHistRepository documentosHistSolicitudesRepository;
	private final DocumentosSolicitudRespository documentosSolicitudesRespository;
	
	public void registrarDocEnHistExperianNoObtenido(EventoProcesadoDto dto) {

        DocumentosSolicitudHistEntity entity =
                DocumentosSolicitudHistEntity.builder()
                        .queryId(dto.getQueryId())
                        .documentCode(dto.getDocumento().getDocumentCode())
                        .estadoDocumento("EXPERIAN_KO")
                        .fechaAlta(OffsetDateTime.now(ZoneOffset.UTC))
                        .build();

        documentosHistSolicitudesRepository.save(entity);
    }
	
	public void registrarDocumentoPteDescarga (EventoProcesadoDto dto) {
		
		
		DocumentosSolicitudEntity entity =
				DocumentosSolicitudEntity.builder()
					.queryId(dto.getQueryId())
					.documentCode(dto.getDocumento().getDocumentCode())
					.estadoDocumento("PTE_DESCARGA")
					.build();
		
		documentosSolicitudesRespository.save(entity);
					
	}

}
