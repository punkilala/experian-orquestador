package bs.experian.orquestador.infrastructure.persistence.solicitud;

import static bs.experian.orquestador.domain.constants.ExperianConstants.*;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.domain.enums.DomainEnum.EstadoInterno;
import bs.experian.orquestador.infrastructure.dto.integracion.SolicitudNuevaRequest;
import bs.experian.orquestador.infrastructure.dto.orquestador.SolicitudNuevaResponse;
import bs.experian.orquestador.infrastructure.dto.orquestador.SolicitudesActivasResponse;
import bs.experian.orquestador.infrastructure.exceptions.NonRetryableProcessingException;
import bs.experian.orquestador.infrastructure.mappers.OrquestadorMapper;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProcesadorSolicitudesRepository {
	
	private final SolicitudRepository solicitudRepository;
	private final OrquestadorMapper mapper;
	
	/**
	 * De un evento si existe solicitud o esta ya esta en estado finalizada
	 * @param evento
	 */
	public void comprobarSolicitud(EventoDto evento) {
		 SolicitudEntity entity = solicitudRepository.findById(evento.getQueryId())
				 .orElseThrow(()-> new NonRetryableProcessingException("Solicitud no existe ", "Solicitud no encontrada para queryId " + evento.getQueryId()));
		 
		 if(ESTADOS_EXPERIAN_CANCELADA_ERROR.contains(entity.getEstadoInterno())) {
			 throw new NonRetryableProcessingException("Solicitud en estado final", 
					 "Solicitud %s en estado final %s ".formatted(evento.getQueryId(), entity.getEstadoInterno()));
		 }
		 
		 if(STATUS_SUCCESS.equals(entity.getEstadoExperian()) 
				 && ESTADOS_FINALES_DOCUMENTACION.contains(entity.getSubEstadoExperian())
				 && ! TIPO_EVENTOS_POSIBLES_TRAS_FIN_DOCUMENTACION.contains(evento.getEventType())){
			 throw new NonRetryableProcessingException("Evento no permitido", 
					 "Actualmente la Solicitud no permite esta accion por encontrarse en el estado final: %s (%s)"
					 .formatted(entity.getEstadoExperian(), entity.getSubEstadoExperian()));
		 }
		 
		 evento.getEventData().setSolicitudActual(entity);
	}
	
	/**
	 * obtener una solicitud por su pk
	 * @param queryId
	 * @return
	 */
	public SolicitudEntity getSolicitud(String queryId) {
		return solicitudRepository.findById(queryId)
				 .orElse(null);
	}
	
	/**
	 * crear nueva solicitud a experian
	 * @param request
	 * @param response
	 */
	public void guardarSolicitud(SolicitudNuevaRequest request, SolicitudNuevaResponse response) {

        SolicitudEntity entity = new SolicitudEntity();
        entity.setQueryId(response.getQueryId());
        entity.setRequestReference(response.getRequestReference());

        entity.setFechaCreacion(OffsetDateTime.now());

        entity.setEstadoExperian(response.getStatus());
        entity.setSubEstadoExperian(response.getSubstatus());
        entity.setEstadoInterno(EstadoInterno.CREADA);

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
	 * Actualizar la solicitud al procesar un evento intermedio
	 * @param queryId
	 * @param dto
	 */
	public void actualizarEstadoSolicitud(EventoDto evento) {
			
		boolean cambio = false;
	
		SolicitudEntity solicitud = evento.getEventData().getSolicitudActual();
		
		if(evento.getEventData().getOrigen() == null && 
				!(STATUS_SUCCESS.equals(solicitud.getEstadoExperian()) && ESTADOS_FINALES_DOCUMENTACION.contains(solicitud.getSubEstadoExperian()))) {
			//es evento intermedio experian
			
			solicitud.setEstadoExperian(evento.getEventData().getStatus() == null ? solicitud.getEstadoExperian() : evento.getEventData().getStatus() );
			solicitud.setSubEstadoExperian(evento.getEventData().getSubstatus() == null ? solicitud.getSubEstadoExperian() : evento.getEventData().getSubstatus());
			cambio = true;
		}
		
		if (EstadoInterno.CREADA.equals(solicitud.getEstadoInterno())) {
			solicitud.setEstadoInterno(EstadoInterno.EN_PROCESO);
			cambio = true;
		}
		
		if(EstadoInterno.EN_PROCESO.equals(solicitud.getEstadoInterno()) && EVENT_NEW_DOCUMENT_AVAILABLE.equals(evento.getEventType())) {
			solicitud.setEstadoInterno(EstadoInterno.CUSTODIA_EN_PROCESO);
			cambio = true;
		}
		
		if(cambio) {
			solicitud.setFechaUltimaActualizacion(OffsetDateTime.now());
			solicitudRepository.save(solicitud);
		}
			 
	}
	
	/**
	 * Actualizar solicitud para estados finales
	 * @param evento
	 */
	public void actualizarEstadoFinalSolicitud (EventoDto evento) {
		solicitudRepository.save(evento.getEventData().getSolicitudActual());
	}
	
	public SolicitudesActivasResponse obtenerSolicitudesActivas (String idFiscal) {
		SolicitudesActivasResponse response = new SolicitudesActivasResponse();
		response.setIdFiscal(idFiscal);
		
		//buscar solicitudes en curso
		List<SolicitudEntity> solicitudes = solicitudRepository.findByEstadoExperianInAndPersonId(
				List.of(STATUS_CREATED, STATUS_PROCESSING), 
				idFiscal
		);
		response.setSolicitudesEnCuros(mapper.entityListToDtoList(solicitudes));
		response.setNumSolicitudesEncurso(solicitudes.size());
		
		//buscar solicitudes vigentes
		OffsetDateTime fechaLimite = OffsetDateTime.now().minusDays(90);

		solicitudes = solicitudRepository.findSolicitudesVigentes(
				STATUS_SUCCESS,
                List.of(SUBSTATUS_ALL_DOCUMENTS_DOWNLOADED, SUBSTATUS_PARTIAL_DOCUMENTS_DOWNLOADED),
                fechaLimite,
                idFiscal
        );
		response.setSolicitudesVigentes(mapper.entityListToDtoList(solicitudes));
		response.setNumSolicitudesVigentes(solicitudes.size());
		
		return response;
		
	}
		
}
