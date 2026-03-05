package bs.experian.orquestador.infrastructure.worker;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import bs.experian.orquestador.application.eventos.EventoProcesador;
import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import bs.experian.orquestador.infrastructure.persistence.eventos.EventoExperianVivoEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RouterEventosExperian {
	
	private final ObjectMapper objectMapper;
	private final List<EventoProcesador> procesadores;
	
	public EventoProcesadoDto procesar(EventoExperianVivoEntity evento) throws JsonProcessingException {
		
		EventoProcesadoDto eventoProcesadoDto = informarEventoProcesadoDto(evento);
		
		for (EventoProcesador procesador : procesadores) {
			if(procesador.aplica(eventoProcesadoDto)) {
				procesador.procesar(eventoProcesadoDto);
				break;
			}
		}
			
		return eventoProcesadoDto;
	}
	
	public EventoProcesadoDto informarEventoProcesadoDto (EventoExperianVivoEntity evento) throws JsonProcessingException {
		JsonNode root = objectMapper.readTree(evento.getPayloadJson());
		String eventType = root.path("eventType").asText();
		
		JsonNode eventData = root.path("eventData");
		String estado = eventData.path("status").asText();
		String subEstado = eventData.path("substatus").asText();
		String codeDocument = eventData.path("documentCode").asText();
		String pdfDocumentUrl = eventData.path("pdfDocumentUrl").asText();
		String jsonDocumentUrl = eventData.path("jsonDocumentUrl").asText();
		
		return  EventoProcesadoDto.builder()
				.idLong(evento.getId())
				.queryId(evento.getQueryId())
				.eventType(eventType)
				.estadoExperian(estado)
				.subestadoExperian(subEstado)
				.documento(EventoProcesadoDto.Documento.builder()
						.documentCode(codeDocument)
						.pdfDocumentUrl(pdfDocumentUrl)
						.jsonDocumentUrl(jsonDocumentUrl)
						.build())
				.procesado(false)
				.build();
		
	}
}
