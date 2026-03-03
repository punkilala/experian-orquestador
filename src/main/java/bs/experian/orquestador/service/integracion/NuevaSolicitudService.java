package bs.experian.orquestador.service.integracion;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import bs.experian.orquestador.config.OrquestadorProperties;
import bs.experian.orquestador.dto.integracion.SolicitudNuevaRequest;
import bs.experian.orquestador.dto.orquestador.SolicitudNuevaResponse;
import bs.experian.orquestador.exceptions.WebclientErrorMapper;
import bs.experian.orquestador.service.aplicacion.OrquestadorTxService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NuevaSolicitudService{
	
	private final WebClient webClient;
	private final OrquestadorProperties props;
	private final OrquestadorTxService txService;

	
	public SolicitudNuevaResponse crearSolicitud(SolicitudNuevaRequest request) {
		//obtener properties
		String urlSolicitudes = props.getApi().getIntegracion().getExperianSolicitudesUrl();
		
		//llamada integracion
		SolicitudNuevaResponse response = webClient
				.post()
				.uri(urlSolicitudes)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.retrieve()
				.onStatus(HttpStatusCode::isError,
					    resp -> WebclientErrorMapper.toAgoraException(resp, "Error llamando a integracion-experian al crear solicitud"))
				.bodyToMono(SolicitudNuevaResponse.class)
				.block();
		
		txService.crearSolicitud(request, response);
		
		return response;
	}

}
