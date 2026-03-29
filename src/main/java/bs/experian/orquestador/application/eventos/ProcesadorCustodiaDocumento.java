package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.*;

import org.springframework.stereotype.Component;
import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.infrastructure.persistence.documentos.ProcesadorDocumentoRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcesadorCustodiaDocumento implements EventoProcesador {
	
	private final ProcesadorDocumentoRepository procesadorDocumentoRepository;
	
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
	}

}



