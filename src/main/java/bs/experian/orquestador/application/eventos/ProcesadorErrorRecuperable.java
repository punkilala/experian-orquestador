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
public class ProcesadorErrorRecuperable implements EventoProcesador {
	
	private static final Set<String> SUBSTATUS_ERRORES_RECUPERABLES = Set.of(
			SUBSTATUS_NINGUNO,
			SUBSTATUS_CREATED,
			SUBSTATUS_INVALID_IDENTIFYING,
			SUBSTATUS_REJECTED_IDENTITY,
			SUBSTATUS_ABANDONED_AFTER_WAITING_OTP_AEAT,
			SUBSTATUS_ABANDONED_AFTER_INVALID_OTP_AEAT
		);

	@Override
	public boolean aplica(EventoDto evento) {
		
		return EVENT_STATUS_CHANGED.equals(evento.getEventType())
		        && STATUS_ERROR.equals(evento.getEventData().getStatus())
		        && SUBSTATUS_ERRORES_RECUPERABLES.contains(evento.getEventData().getSubstatus());
	}

	@Override
	public void procesar(EventoDto evento)  {
		
		evento.getEventData().setEventoFinal(true);
		evento.getEventData().setEstadoInternoFinal(ERROR.name());
		
	}

}
