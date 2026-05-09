package bs.experian.orquestador.infrastructure.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;

import bs.experian.orquestador.infrastructure.exceptions.NonRetryableProcessingException;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

@Configuration
public class KafkaAvroProducerConfig {
	
	@Bean(name = "avroKafkaTemplate")
    public KafkaTemplate<String, Object> avroKafkaTemplate() {

        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put("schema.registry.url", "http://localhost:8081");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        
        ProducerFactory<String, Object> producerFactory =
                new DefaultKafkaProducerFactory<>(props);
        
        return new KafkaTemplate<>(producerFactory);
    }
	
	@Bean
    @Primary
    public KafkaTemplate<String, String> kafkaTemplate() {

        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        ProducerFactory<String, String> producerFactory =
                new DefaultKafkaProducerFactory<>(props);

        return new KafkaTemplate<>(producerFactory);
    }
	
	@RetryableTopic(
	        attempts = "4",
	        backoff = @Backoff(delay = 3_600_000),
	        exclude = {NonRetryableProcessingException.class},
	        dltStrategy = DltStrategy.FAIL_ON_ERROR,
	        autoCreateTopics = "true",
	        kafkaTemplate = "avroKafkaTemplate"
	)

    @Bean(name = "defaultRetryTopicKafkaTemplate")
    public KafkaTemplate<String, String> defaultRetryTopicKafkaTemplate(
            KafkaTemplate<String, String> kafkaTemplate) {
        return kafkaTemplate;
    }
}
