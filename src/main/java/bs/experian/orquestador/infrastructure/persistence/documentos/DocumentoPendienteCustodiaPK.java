package bs.experian.orquestador.infrastructure.persistence.documentos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DocumentoPendienteCustodiaPK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String queryId;
    private String documentCode;
}