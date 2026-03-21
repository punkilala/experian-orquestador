package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.*;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import bs.experian.orquestador.application.DocumentosApplicagtionService;
import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcesadorDocumentoCustodiado implements EventoProcesador {
	private final ObjectMapper objectMapper;
	private final DocumentosApplicagtionService documentosApplicagtionService;
	
	@Override
	public boolean aplica(EventoProcesadoDto evento) {
		return EVENT_TYPE_CUSTODIA.equals(evento.getEventType())
	            && STATUS_CUSTODIA.equals(evento.getEstadoExperian());
	}

	@Override
	public void procesar(EventoProcesadoDto evento) throws JsonProcessingException {
		JsonNode root = objectMapper.readTree(evento.getPayloadJson());
		JsonNode eventData = root.path("eventData");
		
		String codeDocument = eventData.path("documentCode").asText();
		String estadoCustodia = eventData.path("substatus").asText();
		
		evento.setDocumento(
				EventoProcesadoDto.Documento.builder()
						.documentCode(codeDocument)
						.pdfDocument(estadoCustodia)
						.build()
			);
		
		documentosApplicagtionService.actualizarEstadoDocumento(evento);
		evento.setProcesado(true);
		
	}

}
