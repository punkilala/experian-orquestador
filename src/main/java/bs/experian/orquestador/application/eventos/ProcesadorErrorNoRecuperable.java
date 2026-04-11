package bs.experian.orquestador.application.eventos;

import static bs.experian.orquestador.domain.constants.ExperianConstants.*;
import static bs.experian.orquestador.domain.enums.DomainEnum.EstadoInterno.ERROR;
import java.util.Set;

import org.springframework.stereotype.Component;
import bs.experian.orquestador.application.model.evento.EventoDto;
import lombok.RequiredArgsConstructor;

/**
 * Experian comunica que la solicitud ha da un error y este no es recuperable
 */
@Component
@RequiredArgsConstructor
public class ProcesadorErrorNoRecuperable implements EventoProcesador {
	
	private static final Set<String> SUBSTATUS_ERRORES_NO_RECUPERABLES = Set.of(
		    SUBSTATUS_NO_CLAVE_PIN_SERVICE,
		    SUBSTATUS_NO_SMS_SERVICE,
		    SUBSTATUS_FAILED_DOCUMENTS_DOWNLOADED,
		    SUBSTATUS_INVALID_LEGAL_REPRESENTATIVE,
		    SUBSTATUS_REJECTED_RMC_VALIDATION
		);

	@Override
	public boolean aplica(EventoDto evento) {
		
		return EVENT_STATUS_CHANGED.equals(evento.getEventType())
		        && STATUS_ERROR.equals(evento.getEventData().getStatus())
		        && SUBSTATUS_ERRORES_NO_RECUPERABLES.contains(evento.getEventData().getSubstatus());
	}

	@Override
	public void procesar(EventoDto evento)  {
		
		evento.getEventData().setEventoFinal(true);
		evento.getEventData().getSolicitudActual().setEstadoInterno(ERROR);
		evento.getEventData().getSolicitudActual().setEstadoExperian(STATUS_ERROR);
		evento.getEventData().getSolicitudActual().setSubEstadoExperian(evento.getEventData().getSubstatus());
		
	}

}
