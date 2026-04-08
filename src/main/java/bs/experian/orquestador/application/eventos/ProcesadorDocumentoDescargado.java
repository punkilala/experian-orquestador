package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.CUSTODIA_COMPLETA;
import static bs.experian.orquestador.domain.constants.ExperianConstants.CUSTODIA_PARTICAL;
import static bs.experian.orquestador.domain.constants.ExperianConstants.EVENT_DOCUMENTO_DESCARGADO;
import static bs.experian.orquestador.domain.constants.ExperianConstants.STATUS_DOCUMENTO_DESCARGADO;
import static bs.experian.orquestador.domain.constants.ExperianConstants.STATUS_SUCCESS;

import org.springframework.stereotype.Component;

import bs.experian.orquestador.application.SolicitudApplicationService;
import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.infrastructure.dto.integracion.TopicKafkaDocumento;
import bs.experian.orquestador.infrastructure.kafka.produces.KafkaProduceOrdenDocumento;
import bs.experian.orquestador.infrastructure.persistence.documentos.ProcesadorDocumentoRepository;
import bs.experian.orquestador.infrastructure.persistence.solicitud.SolicitudEntity;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcesadorDocumentoDescargado implements EventoProcesador {
		
	private final ProcesadorDocumentoRepository procesadorDocumentoRepository;
	private final KafkaProduceOrdenDocumento kafkaProduceOrdenDocumento;
	private final SolicitudApplicationService solicitudApplicationService;
	
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
		}else {
			//documentos tardios que no se han poidido descargar despues de haber recibido all o partical_documents_donwloade
			SolicitudEntity solicitud = solicitudApplicationService.getSolicitud(evento.getQueryId());
			if(STATUS_SUCCESS.equals(solicitud.getEstadoExperian())){
				recalcularEstado(solicitud, evento);
			}
		}
	}
	
	private void recalcularEstado (SolicitudEntity solicitud, EventoDto evento) {
		if(solicitud.getEstadoInterno().name().equals(CUSTODIA_COMPLETA)){
			evento.getEventData().setEstadoInternoFinal(CUSTODIA_PARTICAL);
		}else {
			evento.getEventData().setEstadoInternoFinal(solicitud.getEstadoInterno().name());
		}
		evento.getEventData().setStatus(solicitud.getEstadoExperian());
		evento.getEventData().setSubstatus(solicitud.getSubEstadoExperian());
		evento.getEventData().setEventoFinal(true);
	}

}
