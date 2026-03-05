package bs.experian.orquestador.domain.services;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import bs.experian.orquestador.infrastructure.persistence.solicitud.SolicitudesDocumentosHistEntity;
import bs.experian.orquestador.infrastructure.persistence.solicitud.SolicitudesDocumentosHistRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentoService {
	
	private final SolicitudesDocumentosHistRepository solicitudesDocumentosHistRepository;
	
	public void registrarExperianKOEnHistorico(EventoProcesadoDto dto) {

        SolicitudesDocumentosHistEntity entity =
                SolicitudesDocumentosHistEntity.builder()
                        .queryId(dto.getQueryId())
                        .documentCode(dto.getDocumento().getDocumentCode())
                        .estadoDocumento("EXPERIAN_KO")
                        .fechaAlta(OffsetDateTime.now(ZoneOffset.UTC))
                        .build();

        solicitudesDocumentosHistRepository.save(entity);
    }

}
