package bs.experian.orquestador.infrastructure.kafka.avro;

import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import static bs.experian.orquestador.application.utils.OrquestadorUtils.stackTraceToString;

import bs.experian.events.avro.DocumentoDescargaOrdenAvro;
import bs.experian.orquestador.infrastructure.exceptions.RetryableProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Topic kafka par ordenar descargar documentos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AvroKafkaProduceOrdenDocumento {
	
	@Qualifier("avroKafkaTemplate")
	private final KafkaTemplate<String, Object> avroKafkaTemplate;
	
	public void publicar(DocumentoDescargaOrdenAvro mensaje, String topic) {

		try {			
			String key = mensaje.getQueryId();
            avroKafkaTemplate.send(topic, key, mensaje).get(10, TimeUnit.SECONDS);
			
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
        } catch (TimeoutException e) {
        	 log.error("###ERR KafkaProduceOrdenDocumento: Error TimeOut al publicar en Kafka. topic={}, queryId={}", topic, mensaje.getQueryId(), stackTraceToString(e,35));
             throw new RetryableProcessingException(
                     "Error al publicar en Kafka",
                     e.getCause() != null ? e.getCause() : e
             );
		}
		
	}

}
