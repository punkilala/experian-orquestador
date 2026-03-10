package bs.experian.orquestador.infrastructure.worker;


import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import bs.experian.orquestador.application.EventoApplicationService;
import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import bs.experian.orquestador.infrastructure.persistence.eventos.entity.EventoExperianVivoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventoExperianWorker {

	private final RouterEventosExperian routerEventosExperian;
	private final EventoApplicationService eventoApplicationService;

	
	@Scheduled(fixedDelayString = "15000")
	public void  procesarEventos() {
		System.out.println("Worker ejecutándose...");
		Optional<EventoExperianVivoEntity> eventoOptional = eventoApplicationService.reclamarEvento();
		
		//cola de eventos pte procesar vacia
		if (eventoOptional.isEmpty()) {
			System.out.println("Worker acabo NADA QUE PROCESAR...");
			return;
		}
		
		//evento que se va a procesar
		EventoExperianVivoEntity evento = eventoOptional.get();
		//resultado del proceso
		EventoProcesadoDto eventoProcesado = null; 
		
		try {
			//procesar evento
			eventoProcesado = routerEventosExperian.procesar(evento);
			eventoApplicationService.finalizarEvento(evento, eventoProcesado);
	
    	}catch (IllegalArgumentException | JsonProcessingException | DataIntegrityViolationException e) {
    		System.out.println("FIN Worker IllegalArgumentException Y MAS...");
    		log.error("Worker IllegalArgumentException..", e);
		    //evento no reintentable pasasr a eventos errores finales
    		if (null == eventoProcesado) {
    			eventoProcesado = informarEventoDto(evento);
    		}
    		evento.setErrorCode("ERROR_FUNCIONAL");
    		evento.setErrorMensaje(e.getMessage());
    		eventoApplicationService.eventoNoProcesadoErrorFuncional(evento, eventoProcesado);
		}catch (Exception e) {
			System.out.println("FIN Worker Exception...");
			log.error("Worker Exception..", e);
		    // evento reintentable
			if (null == eventoProcesado) {
    			eventoProcesado = informarEventoDto(evento);
    		}
			eventoApplicationService.reprogramarEvento(evento.getId(), "ERROR_TECNICO", e.getMessage());
		}
		
		System.out.println("Worker acabo DE PROCESAR...");
		
	}
	private EventoProcesadoDto informarEventoDto (EventoExperianVivoEntity evento) {
		try {
			return  routerEventosExperian.informarEventoProcesadoDto(evento);
		} catch (JsonProcessingException e) {
			return new EventoProcesadoDto();
		}
	}
}

