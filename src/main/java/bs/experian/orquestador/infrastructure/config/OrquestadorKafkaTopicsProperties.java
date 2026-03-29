package bs.experian.orquestador.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "app.kafka.topics")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrquestadorKafkaTopicsProperties {
	private String webhookEvents;
    private String descargaOrden;
    private String descargaResultado;
    private String custodiaOrden;
    private String custodiaResultado;
}
