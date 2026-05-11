package bs.experian.orquestador.infrastructure.kafka.avro;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import bs.experian.events.avro.DocumentoDescargaResultAvro;
import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.application.model.evento.PayLoadDto;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProcesadorDescargaDocumento implements TiposEventosProcesador {
	
	@Override
	public boolean soportado(Object evento) {
		return evento instanceof DocumentoDescargaResultAvro;
	}

	@Override
	public EventoDto procesar(Object object) {
		DocumentoDescargaResultAvro eventoKafka = (DocumentoDescargaResultAvro) object;
		ObjectMapper mapper = new ObjectMapper();
		String jsonBonito = "";
		try {
			jsonBonito = mapper
			        .writerWithDefaultPrettyPrinter()
			        .writeValueAsString(eventoKafka);
		} catch (JsonProcessingException e) {
			jsonBonito = eventoKafka.toString();
		}
		
		 return EventoDto.builder()
				 .queryId(eventoKafka.getQueryId())
				 .notificationId(eventoKafka.getNotificationId())
				 .eventType(eventoKafka.getEventType())
				 .eventData(
						 PayLoadDto.builder()
			                .documentCode(eventoKafka.getDocumentCode())
			                .resultDescargaCustodia(eventoKafka.getResultadoDescarga())
			                .pdfEstado(eventoKafka.getPdfStatus())
			                .pdfErrorMsg(eventoKafka.getPdfErrMsg())
			                .jsonEstado(eventoKafka.getJsonStatus())
			                .jsonErrorMsg(eventoKafka.getJsonErrMsg())
			                .payLoad(jsonBonito)
			                .build()
				  )
				 .build();
	}

	
}
