package bs.experian.orquestador.infrastructure.webclient;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import bs.experian.orquestador.infrastructure.config.OrquestadorProperties;
import bs.experian.orquestador.infrastructure.dto.integracion.SolicitudNuevaRequest;
import bs.experian.orquestador.infrastructure.dto.orquestador.SolicitudNuevaResponse;
import bs.experian.orquestador.infrastructure.exceptions.WebclientErrorMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NuevaSolicitudClient{
	
	private final WebClient webClient;
	private final OrquestadorProperties props;
	
	public SolicitudNuevaResponse crearSolicitud(SolicitudNuevaRequest request) {
		//obtener properties
		String urlSolicitudes = props.getApi().getIntegracion().getExperianSolicitudesUrl();
		
		//llamada integracion
		return webClient
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
	}

}
