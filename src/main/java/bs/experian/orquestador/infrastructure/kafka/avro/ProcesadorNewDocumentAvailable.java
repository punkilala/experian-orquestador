package bs.experian.orquestador.infrastructure.kafka.avro;

import org.springframework.stereotype.Service;

import bs.experian.events.avro.ExperianNewDocumentAvailableEventAvro;
import bs.experian.events.avro.ExperianStatusChangedEventAvro;
import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.application.model.evento.PayLoadDto;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProcesadorNewDocumentAvailable implements TiposEventosProcesador {
	
	@Override
	public boolean soportado(Object evento) {
		return evento instanceof ExperianNewDocumentAvailableEventAvro;
	}

	@Override
	public EventoDto procesar(Object object) {
		ExperianNewDocumentAvailableEventAvro eventoKafka  = (ExperianNewDocumentAvailableEventAvro) object;
		return EventoDto.builder()
				 .queryId(eventoKafka.getQueryId())
				 .eventType(eventoKafka.getEventType())
				 .notificationId(eventoKafka.getNotificationId())
				 .eventData(
						 PayLoadDto.builder()
			                .documentCode(eventoKafka.getDocumentCode())
			                .pdfDocumentUrl(eventoKafka.getPdfDocumentUrl())
			                .jsonDocumentUrl(eventoKafka.getJsonDocumentUrl())
			                .payLoad(eventoKafka.getPayloadExperianJson())
			                .build()
				  )
				 .build();		
	}

	
}
