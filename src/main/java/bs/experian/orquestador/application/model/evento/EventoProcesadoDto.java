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
public class EventoProcesadoDto {
	private Long idLong;
	private String queryId;
	private String eventType;
	private String estadoExperian;
    private String subestadoExperian;
    private Documento documento;
    private boolean procesado;
    
    @Getter 
    @Setter 
    @AllArgsConstructor 
    @NoArgsConstructor 
    @Builder 
    public static class Documento {
    	private String documentCode; 
    	private String pdfDocumentUrl; 
    	private String jsonDocumentUrl;
    	private String errorCode;
    	private String errorMessage;
    }
}

