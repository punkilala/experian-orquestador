package bs.experian.orquestador.application.model.evento;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventoDto {
	private String queryId;
	private String notificationId;
	private String eventType;
	private JsonNode eventData;

}
