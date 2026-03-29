package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.EVENT_DOCUMENTO_DESCARGADO;
import static bs.experian.orquestador.domain.constants.ExperianConstants.STATUS_DOCUMENTO_DESCARGADO;

import org.springframework.stereotype.Component;
import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.infrastructure.dto.integracion.TopicKafkaDocumento;
import bs.experian.orquestador.infrastructure.kafka.produces.KafkaProduceOrdenDocumento;
import bs.experian.orquestador.infrastructure.persistence.documentos.ProcesadorDocumentoRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcesadorDocumentoDescargado implements EventoProcesador {
		
	private final ProcesadorDocumentoRepository procesadorDocumentoRepository;
	private final KafkaProduceOrdenDocumento kafkaProduceOrdenDocumento;
	
	@Override
	public boolean aplica(EventoDto evento) {
		return EVENT_DOCUMENTO_DESCARGADO.equals(evento.getEventType())
	            && STATUS_DOCUMENTO_DESCARGADO.equals(evento.getEventData().getStatus());
	}

	@Override
	public void procesar(EventoDto evento) {
		evento.getEventData().setOrigen("INTEGRACION");
		//actualizar bdd estado documento
		procesadorDocumentoRepository.actualizarResultDocumentoSolicitud(evento);
		//PUBLICA TOPIC PTE_CUSTODIA
		if("PTE_CUSTODIA".equals(evento.getEventData().getPdfEstado())) {
			TopicKafkaDocumento mensaje = new TopicKafkaDocumento();
			mensaje.setQueryId(evento.getQueryId());
			mensaje.setNotificationId(evento.getNotificationId());
			mensaje.setDocumentCode(evento.getEventData().getDocumentCode());
			
			kafkaProduceOrdenDocumento.publicar(mensaje, "documento.custodia.orden");
		}
	}

}
