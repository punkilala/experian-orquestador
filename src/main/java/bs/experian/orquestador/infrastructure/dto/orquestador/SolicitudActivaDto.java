package bs.experian.orquestador.infrastructure.dto.orquestador;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudActivaDto {
	private String queryId;
	private OffsetDateTime fechaCreacion;
	private String estadoExperian;
	private String subEstadoExperian;
	private String estadoInterno;
	private String oficinaGestor;
	private String userNameGestor;
	private String packDocumental;
}
