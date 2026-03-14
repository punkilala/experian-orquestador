package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.EVENT_DOCUMENTO_DESCARGADO;
import static bs.experian.orquestador.domain.constants.ExperianConstants.STATUS_DOCUMENTO_DESCARGADO;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import bs.experian.orquestador.application.DocumentosApplicagtionService;
import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcesadorDocumentoDescargado implements EventoProcesador {
	
	private final ObjectMapper objectMapper;
	private final DocumentosApplicagtionService documentosApplicagtionService;
	

	@Override
	public boolean aplica(EventoProcesadoDto evento) {
		return EVENT_DOCUMENTO_DESCARGADO.equals(evento.getEventType())
	            && STATUS_DOCUMENTO_DESCARGADO.equals(evento.getEstadoExperian());
	}

	@Override
	public void procesar(EventoProcesadoDto evento) throws JsonProcessingException {
		JsonNode root = objectMapper.readTree(evento.getPayloadJson());
		JsonNode eventData = root.path("eventData");
		
		String codeDocument = eventData.path("documentCode").asText();
		Boolean pdfDocument = eventData.path("pdfDocument").asBoolean();
		String jsonDocument = eventData.path("jsonDocument").asText();
		
		evento.setDocumento(
				EventoProcesadoDto.Documento.builder()
						.documentCode(codeDocument)
						.pdfDocument(pdfDocument)
						.jsonDocument(jsonDocument)
						.build()
			);
		
		documentosApplicagtionService.documentoDescargado(evento);
		evento.setProcesado(true);
	}

}
