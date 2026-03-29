package bs.experian.orquestador.infrastructure.dto.integracion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopicKafkaDocumento {
	
	private String queryId;
	private String notificationId;
	private String documentCode;
	private String pdfUrl;
	private String jsonUrl;
	

}
