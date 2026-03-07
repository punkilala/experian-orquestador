package bs.experian.orquestador.infrastructure.persistence.documentos;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DocumentosSolicitudHistPK implements Serializable {

	private static final long serialVersionUID = 1L;
	private String queryId;
    private String documentCode;
}
