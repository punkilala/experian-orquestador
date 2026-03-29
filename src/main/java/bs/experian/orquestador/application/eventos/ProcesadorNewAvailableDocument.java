package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.EVENT_NEW_DOCUMENT_AVAILABLE;
import static bs.experian.orquestador.domain.constants.ExperianConstants.STATUS_PROCESSING;
import static bs.experian.orquestador.domain.constants.ExperianConstants.SUBSTATUS_NEW_DOCUMENT_AVAILABLE;

import org.springframework.stereotype.Component;
import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.infrastructure.dto.integracion.TopicKafkaDocumento;
import bs.experian.orquestador.infrastructure.kafka.produces.KafkaProduceOrdenDocumento;
import bs.experian.orquestador.infrastructure.persistence.documentos.ProcesadorDocumentoRepository;
import lombok.RequiredArgsConstructor;

/**
 * Experian comunica que un documento ya esta disponible
 * Se guarda en la tabla de procesos de documentos
 * Se envia a integración para que se descarge
 */
@Component
@RequiredArgsConstructor
public class ProcesadorNewAvailableDocument implements EventoProcesador {
	
	private final KafkaProduceOrdenDocumento kafkaProduceOrdenDocumento;
	private final ProcesadorDocumentoRepository procesadorDocumentoRepository;

	@Override
	public boolean aplica(EventoDto evento) {
		
		return EVENT_NEW_DOCUMENT_AVAILABLE.equals(evento.getEventType())
	            && STATUS_PROCESSING.equals(evento.getEventData().getStatus())
	            && (SUBSTATUS_NEW_DOCUMENT_AVAILABLE.equals(evento.getEventData().getSubstatus()));
	}

	@Override
	public void procesar(EventoDto evento)  {
		
		//registrar documento en la tabla DocumentosSolicitdes y llamar a integracion pdara pasarselo
		TopicKafkaDocumento mensaje = new TopicKafkaDocumento();
		mensaje.setQueryId(evento.getQueryId());
		mensaje.setNotificationId(evento.getNotificationId());
		mensaje.setDocumentCode(evento.getEventData().getDocumentCode());
		mensaje.setJsonUrl(evento.getEventData().getJsonDocumentUrl());
		mensaje.setPdfUrl(evento.getEventData().getPdfDocumentUrl());
		
		//registrar en bdd el nuevo documento a descargar
		procesadorDocumentoRepository.registrarDocumentoPteDescarga(evento);
		//mandar mensaje kafka para que integracion lo intercepte y descarge documentos
		kafkaProduceOrdenDocumento.publicar(mensaje, "documento.descarga.orden");
		
	}

}
