package bs.experian.orquestador.infrastructure.persistence.solicitud;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Repository;

import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.domain.enums.DomainEnum;
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
	 * Actualizar la solicitud al procesar un evento intermedio
	 * @param queryId
	 * @param dto
	 */
	public void actualizarEstadoSolicitud(EventoDto evento) {
			
		boolean cambio = false;
		//solo los eventos de Experian cambian el estado
		if(evento.getEventData().getOrigen() == null && ! STATUS_SUCCESS.equals(evento.getEventData().getSolicitudActual().getEstadoExperian())) {
			evento.getEventData().getSolicitudActual().setEstadoExperian(evento.getEventData().getStatus());
			evento.getEventData().getSolicitudActual().setSubEstadoExperian(evento.getEventData().getSubstatus());
			cambio = true;
		}
		
		if (EstadoInterno.CREADA.equals(evento.getEventData().getSolicitudActual().getEstadoInterno())) {
			evento.getEventData().getSolicitudActual().setEstadoInterno(EstadoInterno.EN_PROCESO);
			cambio = true;
		}
		
		if(EstadoInterno.EN_PROCESO.equals(evento.getEventData().getSolicitudActual().getEstadoInterno()) && DOC_PTE_CUSTODIA.equals(evento.getEventData().getPdfEstado())) {
			evento.getEventData().getSolicitudActual().setEstadoInterno(EstadoInterno.CUSTODIA_EN_PROCESO);
			cambio = true;
		}
		
		if(cambio) {
			evento.getEventData().getSolicitudActual().setFechaUltimaActualizacion(OffsetDateTime.now());
			
			solicitudRepository.save(evento.getEventData().getSolicitudActual());
		}
			 
	}
	
	/**
	 * Actualizar solicitud para estados finales
	 * @param evento
	 */
	public void actualizarDirectoEstadoSolicitud (EventoDto evento) {
		solicitudRepository.save(evento.getEventData().getSolicitudActual());
	}
		
}
