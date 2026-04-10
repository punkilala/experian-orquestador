package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.EVENT_STATUS_CHANGED;
import static bs.experian.orquestador.domain.constants.ExperianConstants.STATUS_CANCELED;
import static bs.experian.orquestador.domain.constants.ExperianConstants.SUBSTATUS_CANCELED;
import static bs.experian.orquestador.domain.enums.DomainEnum.EstadoInterno.CANCELADA;

import org.springframework.stereotype.Component;
import bs.experian.orquestador.application.model.evento.EventoDto;
import lombok.RequiredArgsConstructor;

/**
 * Experian comunica que la solicitud se ha cancelado
 */
@Component
@RequiredArgsConstructor
public class ProcesadorCanceled implements EventoProcesador {
	


	@Override
	public boolean aplica(EventoDto evento) {
		
		return EVENT_STATUS_CHANGED.equals(evento.getEventType())
	            && STATUS_CANCELED.equals(evento.getEventData().getStatus())
	            && (SUBSTATUS_CANCELED.equals(evento.getEventData().getSubstatus()));
	}

	@Override
	public void procesar(EventoDto evento)  {
		
		evento.getEventData().setEventoFinal(true);
		evento.getEventData().getSolicitudActual().setEstadoInterno(CANCELADA);
		evento.getEventData().getSolicitudActual().setEstadoExperian(STATUS_CANCELED);
		evento.getEventData().getSolicitudActual().setSubEstadoExperian(SUBSTATUS_CANCELED);
		
	}

}
