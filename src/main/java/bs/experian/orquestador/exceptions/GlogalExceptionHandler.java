package bs.experian.orquestador.exceptions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlogalExceptionHandler {
	// ===== ERRORES DESERIALIZACIÓN REQUEST =====
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleInvalidFormat(
	        HttpMessageNotReadableException ex,
	        HttpServletRequest request) {

		Throwable root = getRootCause(ex);

        String field = "desconocido";
        String message = "Error de formato en la petición";

        if (root instanceof InvalidFormatException ife) {

            field = extractPath(ife);

            Class<?> targetType = ife.getTargetType();
            Object invalidValue = ife.getValue();

            if (targetType.isEnum()) {

                String allowed = Arrays.stream(targetType.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                message = String.format(
                        "Valor inválido '%s'. Valores permitidos: %s",
                        invalidValue,
                        allowed
                );

            } else {
                message = String.format(
                        "Valor inválido '%s' para el tipo %s",
                        invalidValue,
                        targetType.getSimpleName()
                );
            }

        } else if (root instanceof MismatchedInputException mie) {

            field = extractPath(mie);
            message = "Tipo de dato incorrecto para el campo";

        }

        List<Map<String, String>> body = List.of(
                Map.of(
                        "campo", field,
                        "error", message
                )
        );


        ErrorResponse response = new ErrorResponse(
        		HttpStatus.BAD_REQUEST.value(),
                "error de validacion",
                body,
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(response);
	}
	private String extractPath(JsonMappingException ex) {
        return ex.getPath()
                .stream()
                .map(ref -> ref.getFieldName())
                .filter(Objects::nonNull)
                .collect(Collectors.joining("."));
    }

    private Throwable getRootCause(Throwable ex) {
        while (ex.getCause() != null) {
            ex = ex.getCause();
        }
        return ex;
    }
	// ===== ERRORES VALIDACION =====
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

    	List<Map<String, String>> body = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    Map<String, String> err = new HashMap<>();
                    err.put("campo", error.getField());
                    err.put("error", error.getDefaultMessage());
                    return err;
                })
                .toList();

        ErrorResponse error = new ErrorResponse(
        		HttpStatus.BAD_REQUEST.value(),
                "error de validacion",
                body,
                request.getRequestURI(),
                LocalDateTime.now()
        );

        log.warn("ERR ORQUESTADOR-EXPERIAN validación input path={} msg={}", request.getRequestURI(), "error de validacion");
  
	        //enviar respuesta de error 
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	 }
	 
	 // ===== ERRORES CONTROLADOS =====
	@ExceptionHandler(AgoraException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(
            AgoraException ex,
            HttpServletRequest request){


        ErrorResponse error = new ErrorResponse(
        		ex.getStatus(),
                ex.getMessage(),
                ex.getBody(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        log.error("ERR ORQUESTADOR-EXPERIAN downstream status={} path={} msg={} body={}",
                ex.getStatus(), request.getRequestURI(), ex.getMessage(), ex.getBody());

        //enviar respuesta de error 
        return ResponseEntity.status(ex.getStatus()).body(error);

    }
	
	// ===== ERROR CONEXIONES CAIDAS =====
	@ExceptionHandler({
        org.springframework.web.reactive.function.client.WebClientRequestException.class,
        java.net.ConnectException.class,
        java.util.concurrent.TimeoutException.class,
        io.netty.handler.timeout.ReadTimeoutException.class
	})
	public ResponseEntity<ErrorResponse> handleCommunicationErrors(
	        Exception ex,
	        HttpServletRequest request) {

	    ErrorResponse error = new ErrorResponse(
	            HttpStatus.SERVICE_UNAVAILABLE.value(),
	            "Servicio no disponible",
	            bodyGenerico(ex),
	            request.getRequestURI(),
	            LocalDateTime.now()
	    );

	    log.error("ERR ORQUESTADOR-EXPERIAN comunicación caida", ex);

	    return ResponseEntity
	            .status(HttpStatus.SERVICE_UNAVAILABLE)
	            .body(error);
	}
	
	// ===== ERROR GENÉRICO =====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
        Exception ex,
        HttpServletRequest request){
    	
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error interno del servicio ORQUESTADOR-EXPERIAN",
                bodyGenerico(ex),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        log.error("ERR ORQUESTADOR-EXPERIAN interno", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    // ===== ERRORES BDD =====
    @ExceptionHandler({DataAccessException.class, PersistenceException.class, TransactionSystemException.class})
    public ResponseEntity<ErrorResponse> handleDatabaseError(
            DataAccessException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Error de acceso a datos",
                bodyGenerico(ex),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        log.error("ERR BDD ORQUESTADOR-EXPERIAN path={}", request.getRequestURI(), ex);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }
    
    private Object bodyGenerico (Throwable ex) {
    	Object body = "indefinido";
    	if(null != ex.getCause()) {
    		body = ex.getCause().getMessage();
    	}else if(null != ex.getMessage()) {
    		body = ex.getMessage();
    	}
    	
    	return body;
    }

}
