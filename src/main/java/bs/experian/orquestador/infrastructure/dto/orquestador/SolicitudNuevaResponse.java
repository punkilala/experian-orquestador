package bs.experian.orquestador.infrastructure.dto.orquestador;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudNuevaResponse {
	private String queryId;
    private String requestReference;
    private String status;
    private String substatus;
    private String consentUrl;
    private String consentUrlBoxed;

}
