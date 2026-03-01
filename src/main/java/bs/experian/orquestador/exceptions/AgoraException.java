package bs.experian.orquestador.exceptions;


import lombok.Getter;

@Getter
public class AgoraException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
    private final int status;
    private final Object body;

    public AgoraException(int status, String message, Object body) {
        super(message);
        this.status = status;
        this.body = body;
    }
    
}