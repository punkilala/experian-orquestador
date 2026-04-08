package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.*;

import org.springframework.stereotype.Component;

import bs.experian.orquestador.application.SolicitudApplicationService;
import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.infrastructure.persistence.documentos.ProcesadorDocumentoRepository;
import bs.experian.orquestador.infrastructure.persistence.solicitud.SolicitudEntity;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcesadorCustodiaDocumento implements EventoProcesador {
	
	private final ProcesadorDocumentoRepository procesadorDocumentoRepository;
	private final SolicitudApplicationService solicitudApplicationService;
	
	@Override
	public boolean aplica(EventoDto evento) {
		return EVENT_TYPE_CUSTODIA.equals(evento.getEventType())
	            && STATUS_CUSTODIA.equals(evento.getEventData().getStatus());
	}

	@Override
	public void procesar(EventoDto evento)  {
		evento.getEventData().setOrigen("CUSTODIA");
		//actualizar bdd estado documento
		procesadorDocumentoRepository.actualizarResultDocumentoSolicitud(evento);
		
		//recepcion documento tardio una vez que Experian ya notifico all o partial_documents_downloaded
		SolicitudEntity solicitud = solicitudApplicationService.getSolicitud(evento.getQueryId());
		if(STATUS_SUCCESS.equals(solicitud.getEstadoExperian())) {
			boolean todoOk = solicitud.getEstadoInterno().name().equals(CUSTODIA_COMPLETA);
		    boolean todoKo = solicitud.getEstadoInterno().name().equals(ERROR_CUSTODIA);
		    boolean pdfKo = DOC_CUSTODIA_KO.equals(evento.getEventData().getSubstatus());
		    boolean pdfOk = DOC_CUSTODIA_OK.equals(evento.getEventData().getSubstatus());

		    if ((todoOk && pdfKo) || (todoKo && pdfOk)) {
		        evento.getEventData().setEstadoInternoFinal(CUSTODIA_PARTICAL);
		    }else {
		    	evento.getEventData().setEstadoInternoFinal(solicitud.getEstadoInterno().name());
		    }
			evento.getEventData().setStatus(solicitud.getEstadoExperian());
			evento.getEventData().setSubstatus(solicitud.getSubEstadoExperian());
			evento.getEventData().setEventoFinal(true);
		}
		
	}
}



