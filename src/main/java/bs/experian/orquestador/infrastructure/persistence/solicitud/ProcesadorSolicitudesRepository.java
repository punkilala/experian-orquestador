package bs.experian.orquestador.infrastructure.persistence.solicitud;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Repository;

import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import bs.experian.orquestador.domain.enums.DomainEnum;
import bs.experian.orquestador.domain.enums.DomainEnum.EstadoInterno;
import bs.experian.orquestador.infrastructure.dto.integracion.SolicitudNuevaRequest;
import bs.experian.orquestador.infrastructure.dto.orquestador.SolicitudNuevaResponse;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProcesadorSolicitudesRepository {
	
	private final SolicitudRepository solicitudRepository;
	
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
	public void actualizarEstadoSolicitud(String queryId, EventoProcesadoDto dto) {
		
		SolicitudEntity solicitudEntity = solicitudRepository.findById(queryId)
				.orElseThrow(() ->
					new IllegalStateException("Solicitud no encontrada para queryId: " + queryId));
		
		solicitudEntity.setEstadoExperian(dto.getEstadoExperian());
		solicitudEntity.setSubEstadoExperian(dto.getSubestadoExperian());
		
		if (EstadoInterno.CREADA.equals(solicitudEntity.getEstadoInterno())) {
		solicitudEntity.setEstadoInterno(EstadoInterno.EN_PROCESO);
		}
		
		solicitudEntity.setFechaUltimaActualizacion(OffsetDateTime.now(ZoneOffset.UTC));
			 
	}
		
}
