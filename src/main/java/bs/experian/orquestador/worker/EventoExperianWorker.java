package bs.experian.orquestador.worker;


import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import bs.experian.orquestador.dto.EventoProcesadoDto;
import bs.experian.orquestador.entity.EventoExperianVivoEntity;
import bs.experian.orquestador.service.OrquestadorTxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventoExperianWorker {

	
	private final OrquestadorTxService txService;
	private final EventoExperianProcessor eventoExperianProcessor;

	
	@Scheduled(fixedDelayString = "15000")
	public void  procesarEventos() {
		System.out.println("Worker ejecutándose...");
		Optional<EventoExperianVivoEntity> eventoOptional = txService.reclamarEvento();
		
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
			eventoProcesado = eventoExperianProcessor.procesar(evento);
			txService.finalizarEvento(evento, eventoProcesado);
	
    	}catch (IllegalArgumentException | JsonProcessingException | DataIntegrityViolationException e) {
    		System.out.println("FIN Worker IllegalArgumentException Y MAS...");
    		log.error("Worker IllegalArgumentException..", e);
		    //evento no reintentable pasasr a eventos errores finales
    		evento.setErrorCode("ERROR_FUNCIONAL");
    		evento.setErrorMensaje(e.getMessage());
            txService.eventoConErrorFuncional(evento, eventoProcesado);
		}catch (Exception e) {
			System.out.println("FIN Worker Exception...");
			log.error("Worker Exception..", e);
		    // evento reintentable
		    txService.reprogramarEvento(evento.getId(), "ERROR_TECNICO", e.getMessage());
		}
		
		System.out.println("Worker acabo DE PROCESAR...");
		
	}
}

