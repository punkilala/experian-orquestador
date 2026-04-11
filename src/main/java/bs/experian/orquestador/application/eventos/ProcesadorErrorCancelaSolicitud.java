package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.*;
import static bs.experian.orquestador.domain.enums.DomainEnum.EstadoInterno.ERROR;

import java.util.Set;

import org.springframework.stereotype.Component;
import bs.experian.orquestador.application.model.evento.EventoDto;
import lombok.RequiredArgsConstructor;

/**
 * Experian comunica que la solicitud ha da un error y se debe cancelar la solicitud
 */
@Component
@RequiredArgsConstructor
public class ProcesadorErrorCancelaSolicitud implements EventoProcesador {
	
	private static final Set<String> SUBSTATUS_ERRORES_CANCELAR = Set.of(
			SUBSTATUS_ABANDONED_AFTER_WAIING_OTP_SEGURIDAD_SOCIAL,
			SUBSTATUS_ABANDONED_AFTER_INVALID_OTP_SEGURIDAD_SOCIAL
		);

	@Override
	public boolean aplica(EventoDto evento) {
		
		return EVENT_STATUS_CHANGED.equals(evento.getEventType())
		        && STATUS_ERROR.equals(evento.getEventData().getStatus())
		        && SUBSTATUS_ERRORES_CANCELAR.contains(evento.getEventData().getSubstatus());
	}

	@Override
	public void procesar(EventoDto evento)  {
		evento.getEventData().setEventoFinal(true);
		evento.getEventData().getSolicitudActual().setEstadoInterno(ERROR);
		evento.getEventData().getSolicitudActual().setEstadoExperian(STATUS_ERROR);
		evento.getEventData().getSolicitudActual().setSubEstadoExperian(evento.getEventData().getSubstatus());
		evento.getEventData().getSolicitudActual().setEstadoConsentimiento(evento.getEventData().getSubstatus());
		
	}

}
