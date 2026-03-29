package bs.experian.orquestador.infrastructure.exceptions;

public class NonRetryableProcessingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NonRetryableProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
