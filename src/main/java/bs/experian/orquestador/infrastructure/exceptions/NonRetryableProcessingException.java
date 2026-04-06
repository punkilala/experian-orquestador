package bs.experian.orquestador.infrastructure.exceptions;

public class NonRetryableProcessingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NonRetryableProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
	
	public NonRetryableProcessingException(String message) {
	        super(message);
    }
	
    public NonRetryableProcessingException(String message, String detail) {
        super(message + " - " + detail);
    }
}
