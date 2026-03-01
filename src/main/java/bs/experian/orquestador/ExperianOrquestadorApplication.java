package bs.experian.orquestador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExperianOrquestadorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExperianOrquestadorApplication.class, args);
	}

}
