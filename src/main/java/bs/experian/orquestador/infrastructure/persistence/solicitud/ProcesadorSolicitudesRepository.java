package bs.experian.orquestador.infrastructure.persistence.solicitud;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Repository;

import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.domain.enums.DomainEnum.EstadoInterno;
import bs.experian.orquestador.infrastructure.dto.integracion.SolicitudNuevaRequest;
import bs.experian.orquestador.infrastructure.dto.orquestador.SolicitudNuevaResponse;
import bs.experian.orquestador.infrastructure.exceptions.NonRetryableProcessingException;
import lombok.RequiredArgsConstructor;
import static bs.experian.orquestador.domain.constants.ExperianConstants.*;

@Repository
@RequiredArgsConstructor
public class ProcesadorSolicitudesRepository {
	
	private final SolicitudRepository solicitudRepository;
	
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
		
		if(evento.getEventData().getOrigen() == null && ! STATUS_SUCCESS.equals(solicitud.getEstadoExperian())) {
			//es evento intermedio experian
			solicitud.setEstadoExperian(evento.getEventData().getStatus());
			solicitud.setSubEstadoExperian(evento.getEventData().getSubstatus());
			cambio = true;
		}
		
		if (EstadoInterno.CREADA.equals(solicitud.getEstadoInterno())) {
			solicitud.setEstadoInterno(EstadoInterno.EN_PROCESO);
			cambio = true;
		}
		
		if(EstadoInterno.EN_PROCESO.equals(solicitud.getEstadoInterno()) && DOC_PTE_CUSTODIA.equals(evento.getEventData().getPdfEstado())) {
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
		
}
