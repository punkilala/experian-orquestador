package bs.experian.orquestador.application.model.evento;

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
public class PayLoadDto {
	private String origen;
	private String payLoad;
	private String status;
	private String substatus;
	private String documentCode;
	private String pdfDocumentUrl;
	private String pdfEstado;
	private String jsonDocumentUrl; 
	private String jsonEstado;
	private boolean eventoFinal;
	private String estadoInternoFinal;
}

