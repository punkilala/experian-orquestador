package bs.experian.orquestador.service.evento;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.springframework.stereotype.Service;

import bs.experian.orquestador.dto.EventoProcesadoDto;
import bs.experian.orquestador.entity.EventoExperianErrorEntity;
import bs.experian.orquestador.entity.EventoExperianVivoEntity;
import bs.experian.orquestador.repository.EventoExperianErrorRepository;
import bs.experian.orquestador.repository.EventoExperianVivoRepository;
import bs.experian.orquestador.repository.EventoExperianWorkerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventoService {
	
	private final EventoExperianWorkerRepository eventoExperianWorkerRepository;
	private final EventoExperianErrorRepository eventoExperianErrorRepository;
	private final EventoExperianVivoRepository eventoExperianVivoRepository;
	
	public Optional<EventoExperianVivoEntity> reclamarEvento() {
        return eventoExperianWorkerRepository.reclamarEvento();
    }

    public void reprogramarEvento(Long eventoId, String error, String errorMesg) {
        eventoExperianWorkerRepository.reprogramarEvento(eventoId, error, errorMesg);
    }
    

    public void finalizarEventoTecnico(EventoExperianVivoEntity evento,
                                       EventoProcesadoDto dto) {

        eventoExperianWorkerRepository.finalizarEvento(evento, dto);
    }
    
    /**
	 * Si un evento que el worker no lo puede terminar de finalizar por error funcional
	 * lo pasa a la tabla de errores
	 * lo borrar de la cola de eventos a procesar
	 * Ej: evento ya procesado y que este en la tabla historicos
	 * @param evento
	 * @param eventoProcesadoDto
	 */
    public void eventoConErrorFuncional (EventoExperianVivoEntity evento, EventoProcesadoDto eventoProcesadoDto) {
		//guardar en tabla de errores
		EventoExperianErrorEntity errorEntity = new EventoExperianErrorEntity(
				evento.getId(),
				evento.getQueryId(),
				evento.getNotificationId(),
				evento.getOrigenEvento(),
				evento.getEventType(),
				eventoProcesadoDto.getEstadoExperian(),
				eventoProcesadoDto.getSubestadoExperian(),
				eventoProcesadoDto.getDocumento().getDocumentCode(),
				evento.getPayloadJson(),
				evento.getErrorCode(),
				evento.getErrorMensaje(),
				OffsetDateTime.now(ZoneOffset.UTC)
			);
		eventoExperianErrorRepository.save(errorEntity);
		
		//borrar de tabla viva
		eventoExperianVivoRepository.deleteById(evento.getId());
		
	}
    
    public void borrarDeVivo(Long idEvento) {
        eventoExperianVivoRepository.deleteById(idEvento);
    }

}
