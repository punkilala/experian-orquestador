package bs.experian.orquestador.infrastructure.kafka.avro;

import org.springframework.stereotype.Service;

import bs.experian.events.avro.ExperianStatusChangedEventAvro;
import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.application.model.evento.PayLoadDto;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProcesadorEventoStatusChanged implements TiposEventosProcesador {
	
	@Override
	public boolean soportado(Object evento) {
		return evento instanceof ExperianStatusChangedEventAvro;
	}

	@Override
	public EventoDto procesar(Object object) {
		
		 ExperianStatusChangedEventAvro eventoKafka = (ExperianStatusChangedEventAvro) object;
		 return EventoDto.builder()
				 .queryId(eventoKafka.getQueryId())
				 .eventType(eventoKafka.getEventType())
				 .notificationId(eventoKafka.getNotificationId())
				 .eventData(
						 PayLoadDto.builder()
			                .status(eventoKafka.getStatus())
			                .substatus(eventoKafka.getSubstatus())
			                .payLoad(eventoKafka.getPayloadExperianJson())
			                .build()
				  )
				 .build();
	}

	
}
