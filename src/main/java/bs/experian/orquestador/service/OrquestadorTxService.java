package bs.experian.orquestador.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import bs.experian.orquestador.dto.EventoProcesadoDto;
import bs.experian.orquestador.dto.SolicitudNuevaRequest;
import bs.experian.orquestador.dto.SolicitudNuevaResponse;
import bs.experian.orquestador.entity.EventoExperianErrorEntity;
import bs.experian.orquestador.entity.EventoExperianVivoEntity;
import bs.experian.orquestador.entity.SolicitudEntity;
import bs.experian.orquestador.repository.EventoExperianErrorRepository;
import bs.experian.orquestador.repository.EventoExperianVivoRepository;
import bs.experian.orquestador.repository.EventoExperianWorkerRepository;
import bs.experian.orquestador.repository.SolicitudRepository;
import bs.experian.orquestador.utils.DomainEnum;
import bs.experian.orquestador.utils.DomainEnum.EstadoInterno;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrquestadorTxService {
	
	private final SolicitudRepository solicitudRepository;
	private final EventoExperianWorkerRepository eventoExperianWorkerRepository;
	private final EventoExperianErrorRepository eventoExperianErrorRepository;
	private final EventoExperianVivoRepository eventoExperianVivoRepository;
	
	/**
	 * alta nueva solicitud experian
	 * @param request
	 * @param response
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void crearSolicitud(SolicitudNuevaRequest request, SolicitudNuevaResponse response) {
		SolicitudEntity entity = new SolicitudEntity();
		entity.setQueryId(response.getQueryId());
		entity.setRequestReference(response.getRequestReference());
		
		entity.setFechaCreacion(LocalDateTime.now(ZoneOffset.UTC));
		
		entity.setEstadoExperian(response.getStatus());
		entity.setSubEstadoExperian(response.getSubstatus());
		entity.setEstadoInterno(DomainEnum.EstadoInterno.CREADA);
		
		entity.setOficinaGestor(request.getOfficeCode());
		entity.setUserNameGestor(request.getUsernameGestor());
		
		entity.setPersonCategoy(request.getPersonCategory());
		entity.setPersonId(request.getPersonId());
		entity.setCompanyId(request.getCompanyId());
		
		entity.setPackDocumental(request.getDocumentationPack());
		entity.setOrigin(request.getOrigin());

		solicitudRepository.save(entity);

    }
	
	/**
	 * recuperar un evento de la cola para procesar
	 * @return
	 */
	public Optional<EventoExperianVivoEntity> reclamarEvento (){
		return eventoExperianWorkerRepository.reclamarEvento();
		
	}
	
	
	/**
	 * finalizar el procesamiento de un evento
	 * @param evento
	 * @param eventoProcesadoDto
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void finalizarEvento (EventoExperianVivoEntity evento, EventoProcesadoDto eventoProcesadoDto) {
		
		//actualizar evento
		eventoExperianWorkerRepository.finalizarEvento(evento, eventoProcesadoDto);
		
		//actualizar solicitud
		SolicitudEntity solicitudEntity = solicitudRepository
				.findById(evento.getQueryId())
				.orElseThrow(()-> new IllegalStateException( "Solicitud no encontrada para queryId: " + evento.getQueryId()));
				
		solicitudEntity.setEstadoExperian(eventoProcesadoDto.getEstadoExperian());
		solicitudEntity.setSubEstadoExperian(eventoProcesadoDto.getSubestadoExperian());
		if(solicitudEntity.getEstadoInterno().equals(EstadoInterno.CREADA)){
			solicitudEntity.setEstadoInterno(EstadoInterno.EN_PROCESO);
		}
		solicitudEntity.setFechaUltimaActualizacion(LocalDateTime.now(ZoneOffset.UTC));
		
		//falta si es evento final all/partial actualizar estado solicitud dependiendo de custodia
		
	}
	
	/**
	 * reprogramar un evento por error tecnico
	 * @param evendoId
	 * @param error
	 * @param errorMesg
	 */
	public void reprogramarEvento(Long evendoId, String error, String errorMesg) {
		eventoExperianWorkerRepository.reprogramarEvento(evendoId, error, errorMesg);
	}
	
	/**
	 * Si un evento que el worker no lo puede terminar de finalizar por error funcional
	 * lo pasa a la tabla de errores
	 * lo borrar de la cola de eventos a procesar
	 * Ej: evento ya procesado y que este en la tabla historicos
	 * @param evento
	 * @param eventoProcesadoDto
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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
				eventoProcesadoDto.getDocumentCode(),
				evento.getPayloadJson(),
				evento.getErrorCode(),
				evento.getErrorMensaje(),
				OffsetDateTime.now(ZoneOffset.UTC)
			);
		eventoExperianErrorRepository.save(errorEntity);
		
		//borrar de tabla viva
		eventoExperianVivoRepository.deleteById(evento.getId());
		
	}	

}
