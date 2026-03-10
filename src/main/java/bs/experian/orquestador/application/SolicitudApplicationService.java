package bs.experian.orquestador.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import bs.experian.orquestador.infrastructure.dto.integracion.SolicitudNuevaRequest;
import bs.experian.orquestador.infrastructure.dto.orquestador.SolicitudNuevaResponse;
import bs.experian.orquestador.infrastructure.persistence.solicitud.ProcesadorSolicitudesRepository;
import bs.experian.orquestador.infrastructure.webclient.NuevaSolicitudClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitudApplicationService {
	
	private final ProcesadorSolicitudesRepository operacionesConSolicitudesRepository;
	private final NuevaSolicitudClient nuevaSolicitudService;
	
	/**
	 * Crear una nueva solicitud a experian
	 * @param request
	 * @param response
	 */
	public SolicitudNuevaResponse crearSolicitud(SolicitudNuevaRequest request) {
		//llamar a integracion
		SolicitudNuevaResponse response = nuevaSolicitudService.crearSolicitud(request);
		//registrar en bdd la solicitud creada
		operacionesConSolicitudesRepository.guardarSolicitud(request, response);
		
		return response;
	}
	
	/**
	 * Actualizar estado solicitud al procesar un evento
	 * @param queryId
	 * @param dto
	 */
	@Transactional
	public void actualizarEstadoSolicitud (String queryId, EventoProcesadoDto dto) {
		operacionesConSolicitudesRepository.actualizarEstadoSolicitud(queryId, dto);
		
	}
}
