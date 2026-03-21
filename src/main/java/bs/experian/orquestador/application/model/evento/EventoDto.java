package bs.experian.orquestador.application.model.evento;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventoDto {
	@NotBlank (message = "campo requerido")
	private String queryId;
	
	@NotBlank (message = "campo requerido")
	private String notificationId;
	
	private String origen;
	
	@NotBlank (message = "campo requerido")
	private String eventType;
	
	@NotNull (message = "campo requerido")
	private JsonNode eventData;

}
