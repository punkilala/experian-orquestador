package bs.experian.orquestador.infrastructure.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;

@EnableKafka
@Configuration
public class KafkaAvroConsumerConfig {
    @Bean
    ConsumerFactory<String, Object> avroConsumerFactory() {

	    Map<String, Object> props = new HashMap<>();

	    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
	    props.put(ConsumerConfig.GROUP_ID_CONFIG, "orquestador-avro-clean-1");
	    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
	    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
	    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
	    props.put("schema.registry.url", "http://localhost:8081");
	    props.put("specific.avro.reader", true);
	    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
	    

	    return new DefaultKafkaConsumerFactory<>(props);
	}

    @Bean(name = "avroKafkaListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<String, Object> avroKafkaListenerContainerFactory() {

	    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
	            new ConcurrentKafkaListenerContainerFactory<>();

	    factory.setConsumerFactory(avroConsumerFactory());
	    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

	    return factory;
	}

}
