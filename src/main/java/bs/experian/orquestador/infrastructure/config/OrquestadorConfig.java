package bs.experian.orquestador.infrastructure.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;

@Configuration
public class OrquestadorConfig {
	
	@Bean
    public WebClient webClient(OrquestadorProperties props) {

        Integer timeoputrequest = props.getApi().getTimeoutrequest();
        Integer timepoutresponse = props.getApi().getTimeoutresponse();

        if (timeoputrequest == null) timeoputrequest = 5000;
        if (timepoutresponse == null) timepoutresponse = 10000;

        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoputrequest)
            .responseTimeout(Duration.ofMillis(timepoutresponse));

        return WebClient.builder()
            .baseUrl(props.getApi().getIntegracion().getBaseUrl())
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }

}
