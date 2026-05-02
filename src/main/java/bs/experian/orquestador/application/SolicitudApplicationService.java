package bs.experian.orquestador.application;

import org.springframework.stereotype.Service;

import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.infrastructure.dto.integracion.SolicitudNuevaRequest;
import bs.experian.orquestador.infrastructure.dto.orquestador.SolicitudNuevaResponse;
import bs.experian.orquestador.infrastructure.dto.orquestador.SolicitudesActivasResponse;
import bs.experian.orquestador.infrastructure.persistence.solicitud.ProcesadorSolicitudesRepository;
import bs.experian.orquestador.infrastructure.persistence.solicitud.SolicitudEntity;
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
	 * De un evento si existe solicitud o esta ya esta en estado finalizada
	 * @param evento
	 */
	public void comprobarSolicitud(EventoDto evento) {
		operacionesConSolicitudesRepository.comprobarSolicitud(evento);
	}
	
	/**
	 * obtener una solicitud por su pk
	 * @param queryId
	 * @return
	 */
	public SolicitudEntity getSolicitud(String queryId) {
		return operacionesConSolicitudesRepository.getSolicitud(queryId);
	}
	
	/**
	 * saber si hay solicitudes en curso y vigentes (<90 días)
	 * @param idFiscal
	 * @return
	 */
	public SolicitudesActivasResponse obtenerSolicitudesActivas (String idFiscal) {
		return operacionesConSolicitudesRepository.obtenerSolicitudesActivas(idFiscal);
		
	}
}
