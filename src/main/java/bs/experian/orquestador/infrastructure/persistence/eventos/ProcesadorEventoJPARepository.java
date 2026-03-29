package bs.experian.orquestador.infrastructure.persistence.eventos;


import java.time.OffsetDateTime;

import org.springframework.stereotype.Repository;

import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.application.model.evento.PayLoadDto;
import bs.experian.orquestador.infrastructure.persistence.eventos.entity.EventoExperianHistRepository;
import bs.experian.orquestador.infrastructure.persistence.eventos.entity.EventosExperianHistEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProcesadorEventoJPARepository {
	
	private final EventoExperianHistRepository eventoExperianHistRepository;
	
	
	
	/**
	 * Mover el evento recibido de kafka al historico
	 * @param evento
	 * @param procesado
	 */
	public void moverEventoProcesadoToHist(EventoDto evento, String procesado, String errCode, String errMsj ) {
		
		if(null == evento.getEventData()) {
			evento.setEventData(new PayLoadDto());
		}
		String origen = null == evento.getEventData().getOrigen() ? "EXPERIAN" : evento.getEventData().getOrigen();
		
		EventosExperianHistEntity entity = new EventosExperianHistEntity();
		entity.setQueryId(evento.getQueryId());
		entity.setNotificationId(evento.getNotificationId());
		entity.setOrigenEvento(origen);
		entity.setEventType(evento.getEventType());
		entity.setEstadoExperian(evento.getEventData().getStatus());
		entity.setSubestadoExperian(evento.getEventData().getSubstatus());
		entity.setDocumentCode(evento.getEventData().getDocumentCode());
		entity.setPayloadJson(evento.getEventData().getPayLoad());
		entity.setFechaAlta(OffsetDateTime.now());
		entity.setFechaProcesado(OffsetDateTime.now());
		entity.setResultadoProceso(procesado);
		entity.setErrorCode(errCode);
		entity.setErrorMensaje(errMsj);
		
		eventoExperianHistRepository.save(entity);	
	}
}
