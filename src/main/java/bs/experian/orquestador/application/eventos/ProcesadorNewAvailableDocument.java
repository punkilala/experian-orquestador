package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.EVENT_NEW_DOCUMENT_AVAILABLE;
import static bs.experian.orquestador.domain.constants.ExperianConstants.STATUS_PROCESSING;
import static bs.experian.orquestador.domain.constants.ExperianConstants.SUBSTATUS_NEW_DOCUMENT_AVAILABLE;

import org.springframework.stereotype.Component;

import bs.experian.events.avro.DocumentoDescargaOrdenAvro;
import bs.experian.orquestador.application.EventoApplicationService;
import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.infrastructure.kafka.avro.AvroKafkaProduceOrdenDocumento;
import lombok.RequiredArgsConstructor;

/**
 * Experian comunica que un documento ya esta disponible
 * Se guarda en la tabla de procesos de documentos
 * Se envia a integración para que se descarge
 */
@Component
@RequiredArgsConstructor
public class ProcesadorNewAvailableDocument implements EventoProcesador {
	
	private final AvroKafkaProduceOrdenDocumento avroKafkaProduceOrdenDocumento;
	private final EventoApplicationService eventoApplicationService;

	@Override
	public boolean aplica(EventoDto evento) {
		
		return EVENT_NEW_DOCUMENT_AVAILABLE.equals(evento.getEventType());
	}

	@Override
	public void procesar(EventoDto evento)  {
		evento.getEventData().setStatus(STATUS_PROCESSING);
		evento.getEventData().setSubstatus(SUBSTATUS_NEW_DOCUMENT_AVAILABLE);
		
		//finalizar evento
		eventoApplicationService.finalizarEventoConDocumento(evento);
		
		//mandar mensaje kafka para que integracion lo intercepte y descarge documentos
		DocumentoDescargaOrdenAvro mensaje = DocumentoDescargaOrdenAvro.newBuilder()
				.setQueryId(evento.getQueryId())
				.setNotificationId(evento.getNotificationId())
				.setDocumentCode(evento.getEventData().getDocumentCode())
				.setPdfUrl(evento.getEventData().getPdfDocumentUrl())
				.setJsonUrl(evento.getEventData().getJsonDocumentUrl())
				.build();
		avroKafkaProduceOrdenDocumento.publicar(mensaje, "documento.descarga.orden.avro.v1");
		
	}

}
