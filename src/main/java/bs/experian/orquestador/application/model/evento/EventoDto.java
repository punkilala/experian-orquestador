package bs.experian.orquestador.application.model.evento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventoDto {
	@NotBlank (message = "campo requerido")
	private String queryId;
	
	@NotBlank (message = "campo requerido")
	private String notificationId;
	
	@NotBlank (message = "campo requerido")
	private String eventType;
	
	@NotNull (message = "campo requerido")
	private PayLoadDto eventData;
	

}
