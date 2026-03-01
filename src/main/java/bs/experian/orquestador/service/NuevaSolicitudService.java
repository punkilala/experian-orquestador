package bs.experian.orquestador.service;

import bs.experian.orquestador.dto.SolicitudNuevaRequest;
import bs.experian.orquestador.dto.SolicitudNuevaResponse;

public interface NuevaSolicitudService {
	SolicitudNuevaResponse crearSolicitud (SolicitudNuevaRequest request);

}
