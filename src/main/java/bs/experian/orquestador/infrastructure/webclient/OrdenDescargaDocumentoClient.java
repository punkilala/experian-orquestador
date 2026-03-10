package bs.experian.orquestador.infrastructure.webclient;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import bs.experian.orquestador.infrastructure.config.OrquestadorProperties;
import bs.experian.orquestador.infrastructure.dto.integracion.DescargaDocumentoRequest;
import bs.experian.orquestador.infrastructure.exceptions.WebclientErrorMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdenDescargaDocumentoClient {
	
	private final WebClient webClient;
	private final OrquestadorProperties props;
	
	public void ordenarDescarga(EventoProcesadoDto evento) {
		
		String urlBase = props.getApi().getIntegracion().getBaseUrl();
		String urlDescargaDoc = props.getApi().getIntegracion().getExperianDescargaDocumentoUrl();
		
		DescargaDocumentoRequest request = new DescargaDocumentoRequest(
				evento.getQueryId(), 
				evento.getDocumento().getDocumentCode(), 
				evento.getDocumento().getPdfDocumentUrl(), 
				evento.getDocumento().getJsonDocumentUrl());
				
		
		webClient.post()
			.uri(urlBase + urlDescargaDoc)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.retrieve()
			.onStatus(HttpStatusCode::isError,
				    resp -> WebclientErrorMapper.toAgoraException(resp, "Error llamando a integracion-experian para "
				    		+ "orden de descarga documentos"))
			.toBodilessEntity()
			.block();
	}
}
