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
		 
		 if(ESTADOS_FINALES_EXPERIAN_KO.contains(entity.getEstadoInterno())) {
			 throw new NonRetryableProcessingException("Solicitud en estado final", 
					 "Solicitud %s en estado final %s ".formatted(evento.getQueryId(), entity.getEstadoInterno()));
		 }
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
	 * Actualizar la solicitud al procesar un evento
	 * @param queryId
	 * @param dto
	 */
	public void actualizarEstadoSolicitud(EventoDto evento) {
		
		SolicitudEntity solicitudEntity = solicitudRepository.findById(evento.getQueryId())
				.orElseThrow(() ->
				new NonRetryableProcessingException("Solicitud no encontrada", "Solicitud no encontrada para queryId" + evento.getQueryId()));
		
		boolean cambio = false;
		//solo los eventos de Experian cambian el estado
		if(evento.getEventData().getOrigen() == null && ! STATUS_SUCCESS.equals(solicitudEntity.getEstadoExperian())) {
			solicitudEntity.setEstadoExperian(evento.getEventData().getStatus());
			solicitudEntity.setSubEstadoExperian(evento.getEventData().getSubstatus());
			cambio = true;
		}
		
		if (EstadoInterno.CREADA.equals(solicitudEntity.getEstadoInterno())) {
			solicitudEntity.setEstadoInterno(EstadoInterno.EN_PROCESO);
			cambio = true;
		}
		
		if(EstadoInterno.EN_PROCESO.equals(solicitudEntity.getEstadoInterno()) && DOC_PTE_CUSTODIA.equals(evento.getEventData().getPdfEstado())) {
			solicitudEntity.setEstadoInterno(EstadoInterno.CUSTODIA_EN_PROCESO);
			cambio = true;
		}
		
		if(cambio) {
			solicitudEntity.setFechaUltimaActualizacion(OffsetDateTime.now());
			
			solicitudRepository.save(solicitudEntity);
		}
			 
	}
	
	/**
	 * Actualizar solicitud para estados finales
	 * @param queryId
	 * @param estadoInterno
	 */
	public void actualizarDirectoEstadoSolicitud (String queryId, String estadoExperian, String subEstadoExperian, String estadoInterno) {
		SolicitudEntity solicitudEntity = solicitudRepository.findById(queryId)
				.orElseThrow(() ->
					new NonRetryableProcessingException("Solicitud no encontrada", "Solicitud no encontrada para queryId" + queryId));
		
		solicitudEntity.setEstadoExperian(estadoExperian == null ? solicitudEntity.getEstadoExperian() : estadoExperian);
		solicitudEntity.setSubEstadoExperian(subEstadoExperian == null ? solicitudEntity.getSubEstadoExperian() : subEstadoExperian);
		solicitudEntity.setEstadoInterno(estadoInterno == null ? solicitudEntity.getEstadoInterno() : DomainEnum.EstadoInterno.valueOf(estadoInterno));
		solicitudEntity.setFechaUltimaActualizacion(OffsetDateTime.now());
		
		solicitudRepository.save(solicitudEntity);
	}
		
}
