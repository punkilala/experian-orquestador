package bs.experian.orquestador.domain.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import bs.experian.orquestador.domain.enums.DomainEnum;
import bs.experian.orquestador.domain.enums.DomainEnum.EstadoInterno;
import bs.experian.orquestador.infrastructure.dto.integracion.SolicitudNuevaRequest;
import bs.experian.orquestador.infrastructure.dto.orquestador.SolicitudNuevaResponse;
import bs.experian.orquestador.infrastructure.persistence.solicitud.SolicitudEntity;
import bs.experian.orquestador.infrastructure.persistence.solicitud.SolicitudRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolicitudService {
	private final SolicitudRepository solicitudRepository;
	
	 @Transactional
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
	 
	 public void actualizarDesdeEvento(String queryId, EventoProcesadoDto dto) {

		SolicitudEntity solicitudEntity = solicitudRepository.findById(queryId)
				.orElseThrow(() ->
					new IllegalStateException("Solicitud no encontrada para queryId: " + queryId));
		
		solicitudEntity.setEstadoExperian(dto.getEstadoExperian());
		solicitudEntity.setSubEstadoExperian(dto.getSubestadoExperian());
		
		if (EstadoInterno.CREADA.equals(solicitudEntity.getEstadoInterno())) {
		solicitudEntity.setEstadoInterno(EstadoInterno.EN_PROCESO);
		}
		
		solicitudEntity.setFechaUltimaActualizacion(
		LocalDateTime.now(ZoneOffset.UTC));
	}
}

