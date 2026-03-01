package bs.experian.orquestador.exceptions;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorResponse {
	private int status;
    private String message;
    private Object body;
    private String path;
    private LocalDateTime timestamp;
}
