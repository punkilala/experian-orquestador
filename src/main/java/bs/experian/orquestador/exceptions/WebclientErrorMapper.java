package bs.experian.orquestador.exceptions;

import org.springframework.web.reactive.function.client.ClientResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

public class WebclientErrorMapper {
	 public static Mono<? extends Throwable> toAgoraException(ClientResponse resp, String message) {
        int status = resp.statusCode().value();

        return resp.bodyToMono(String.class)
        		.defaultIfEmpty("")
                .map(bodyString -> {
                    Object body;
                    try {
                        body = new ObjectMapper().readValue(bodyString, Object.class);
                    } catch (Exception e) {
                        body = bodyString;
                    }
                    return new AgoraException(status, message, body);
                });
    }
}
