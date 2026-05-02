package bs.experian.orquestador.infrastructure.dto.orquestador;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudesActivasResponse {
	private String idFiscal;
	private int numSolicitudesEncurso;
	private int numSolicitudesVigentes;
	private List<SolicitudActivaDto> solicitudesEnCuros;
	private List<SolicitudActivaDto> solicitudesVigentes;
}
