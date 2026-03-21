package bs.experian.orquestador.infrastructure.dto.cutodia;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustodiaDocumentoRequest {
	private String queryId;
	private String documentCode;
	private String notificationId;
}
