package bs.experian.orquestador.infrastructure.kafka.produces;

import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static bs.experian.orquestador.application.utils.OrquestadorUtils.stackTraceToString;

import bs.experian.orquestador.infrastructure.dto.integracion.TopicKafkaDocumento;
import bs.experian.orquestador.infrastructure.exceptions.NonRetryableProcessingException;
import bs.experian.orquestador.infrastructure.exceptions.RetryableProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * Topic kafka par ordenar descargar documentos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProduceOrdenDocumento {
	
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	
	public void publicar(TopicKafkaDocumento mensaje, String topic) {

		try {
			String key = mensaje.getQueryId();
            String value = objectMapper.writeValueAsString(mensaje);
           
		  kafkaTemplate.send(topic, key, value).get();
			
		} catch (JsonProcessingException e) {
	        log.error("###ERR KafkaProduceOrdenDocumento: Error serializando mensaje Kafka", e);
	        throw  new  NonRetryableProcessingException("Error serializando mensaje Kafka", stackTraceToString(e,35));
		} catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("###ERR KafkaProduceOrdenDocumento: Hilo interrumpido al publicar en Kafka. topic={}, queryId={}", topic, mensaje.getQueryId(), stackTraceToString(e,35));
            throw new RetryableProcessingException("Hilo interrumpido al publicar en Kafka", e);

        } catch (KafkaException | ExecutionException e) {
            log.error("###ERR KafkaProduceOrdenDocumento: Error al publicar en Kafka. topic={}, queryId={}", topic, mensaje.getQueryId(), stackTraceToString(e,35));
            throw new RetryableProcessingException(
                    "Error al publicar en Kafka",
                    e.getCause() != null ? e.getCause() : e
            );
        }
		
	}

}
