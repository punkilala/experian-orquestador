package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.EVENT_STATUS_CHANGED;
import static bs.experian.orquestador.domain.constants.ExperianConstants.STATUS_PROCESSING;
import static bs.experian.orquestador.domain.constants.ExperianConstants.SUBSTATUS_CONSENT_PROCECCS_COMPLETED;

import org.springframework.stereotype.Component;

import bs.experian.orquestador.application.EventoApplicationService;
import bs.experian.orquestador.application.model.evento.EventoDto;
import lombok.RequiredArgsConstructor;

/**
 * Experian comunica consentimiento OK
 */
@Component
@RequiredArgsConstructor
public class ProcesadorConsentProcessCompleted implements EventoProcesador {
	
	private final EventoApplicationService eventoApplicationService;

	@Override
	public boolean aplica(EventoDto evento) {
		
		return EVENT_STATUS_CHANGED.equals(evento.getEventType())
	            && STATUS_PROCESSING.equals(evento.getEventData().getStatus())
	            && (SUBSTATUS_CONSENT_PROCECCS_COMPLETED.equals(evento.getEventData().getSubstatus()));
	}

	@Override
	public void procesar(EventoDto evento)  {
		
		evento.getEventData().getSolicitudActual().setEstadoConsentimiento(evento.getEventData().getSubstatus());
		//finalizar evento
		eventoApplicationService.finalizarEventoConDocumento(evento);
		
	}

}
