package bs.experian.orquestador.service.evento;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import bs.experian.orquestador.dto.EventoDto;
import bs.experian.orquestador.exceptions.AgoraException;
import bs.experian.orquestador.repository.EventoExperianWorkerRepository;
import bs.experian.orquestador.utils.DomainEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecepcionEventoService{
	
	private final ObjectMapper objectMapper;
	private final EventoExperianWorkerRepository eventoExperianRepository;
	
	public void recibirEventoExperian(EventoDto request) {
			String payload = "";
			try {
				payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
				//encolar evento recibido
				eventoExperianRepository.encolarEvento(request, DomainEnum.TiposEventosHistoricos.EXPERIAN.name(), payload);
			} catch (JsonProcessingException e) {
				log.error("ERR ORQUESTADOR-EXPERIAN - evento mal formado al recibirlo de Experian", e);
				throw new AgoraException(HttpStatus.BAD_REQUEST.value(), "evento mal formado", 
						Map.of("campo", "request", "valor", request));
			} catch  (DataIntegrityViolationException e) {
				// solicitud no existe
		    	log.error("ERR ORQUESTADOR-EXPERIAN -  solicitud no encontrada: " + request.getQueryId());
		        throw new AgoraException(HttpStatus.NOT_FOUND.value(), "solicitud no encontrada o notificacion duplicada", 
		        		Map.of("campo", "queryId", "mensaje", request.getQueryId()));

			}

	}

}
