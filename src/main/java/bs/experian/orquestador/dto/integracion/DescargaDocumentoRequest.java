package bs.experian.orquestador.dto.integracion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DescargaDocumentoRequest {
	
	private String queryId;
	private String documentCode;
	private String pdfDocumentUrl;
	private String jsonDocumentUrl;
	

}
