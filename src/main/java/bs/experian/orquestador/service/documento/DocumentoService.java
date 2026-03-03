package bs.experian.orquestador.service.documento;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import bs.experian.orquestador.dto.EventoProcesadoDto;
import bs.experian.orquestador.entity.SolicitudesDocumentosHistEntity;
import bs.experian.orquestador.repository.SolicitudesDocumentosHistRepository;
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
