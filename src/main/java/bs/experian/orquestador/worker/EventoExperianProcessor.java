package bs.experian.orquestador.worker;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import bs.experian.orquestador.dto.EventoProcesadoDto;
import bs.experian.orquestador.entity.EventoExperianVivoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static bs.experian.orquestador.constants.ExperianConstants.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventoExperianProcessor {
	
	private final ObjectMapper objectMapper;
	
	public EventoProcesadoDto procesar(EventoExperianVivoEntity evento) throws Exception {
		
		JsonNode root = objectMapper.readTree(evento.getPayloadJson());
		String eventType = root.path("eventType").asText();
		
		JsonNode evenData = root.path("eventData");
		String estado = evenData.path("status").asText();
		String subEstado = evenData.path("substatus").asText();
		
		String codeDocument = "";
				
		if(EVENT_STATUS_CHANGED.equalsIgnoreCase(eventType)) {
			
			switch (estado) {
				case  STATUS_SUCCESS :
					switch (subEstado) {
						case SUBSTATUS_ALL_DOCUMENTS_DOWNLOADED:
						case SUBSTATUS_PARTIAL_DOCUMENTS_DOWNLOADED:
							//procesar evento
							break;
					default:
						//no reaccionar
						break;
					}
				default:
					//no reaccionar
					break;	
				}
		
		}else if (EVENT_NEW_DOCUMENT_AVAILABLE.equalsIgnoreCase(eventType)) {
			//procesar evento	
		}else {
			log.warn("ORQUESTADOR-EXPERIAN, tipo evento no reconocido:" + eventType);
		}
		
		return new EventoProcesadoDto(
					estado,
					subEstado,
					codeDocument.isEmpty() ? null : codeDocument,
					null
				);
	}
}
