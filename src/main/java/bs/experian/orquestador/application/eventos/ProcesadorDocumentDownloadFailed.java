package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.EVENT_STATUS_CHANGED;
import static bs.experian.orquestador.domain.constants.ExperianConstants.STATUS_ERROR;
import static bs.experian.orquestador.domain.constants.ExperianConstants.SUBSTATUS_DOCUMENT_DWONLOAD_FAILED;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import bs.experian.orquestador.application.DocumentosApplicagtionService;
import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import lombok.RequiredArgsConstructor;

/**
 * Experian comunica que el documento no se ha podibo obtener
 * El documento pasa a historico porque no se va a procesar su descarga
 */
@Component
@RequiredArgsConstructor
public class ProcesadorDocumentDownloadFailed implements EventoProcesador {
	private final ObjectMapper objectMapper;
	private final DocumentosApplicagtionService documentosApplicagtionService;

	@Override
	public boolean aplica(EventoProcesadoDto evento) {
		
		return EVENT_STATUS_CHANGED.equals(evento.getEventType())
	            && STATUS_ERROR.equals(evento.getEstadoExperian())
	            && (SUBSTATUS_DOCUMENT_DWONLOAD_FAILED.equals(evento.getSubestadoExperian()));
	}

	@Override
	public void procesar(EventoProcesadoDto evento) throws JsonProcessingException {
		JsonNode root = objectMapper.readTree(evento.getPayloadJson());
		JsonNode eventData = root.path("eventData");
		
		String codeDocument = eventData.path("documentCode").asText();
		String errorCode = eventData.path("errorCode").asText();
		String errorMessasge = eventData.path("errorMessage").asText();
		
		evento.setDocumento(
			    EventoProcesadoDto.Documento.builder()
			        .documentCode(codeDocument)
			        .errorCode(errorCode)
			        .errorMessage(errorMessasge)
			        .build()
			);
		
		documentosApplicagtionService.registrarDocEnHistExperianNoObtenido(evento);
		evento.setProcesado(true);
	}

}
