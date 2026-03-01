package bs.experian.orquestador.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventoProcesadoDto {
	private String estadoExperian;
    private String subestadoExperian;
    private String documentCode;
    private String errorEvento;
}
