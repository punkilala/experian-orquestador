package bs.experian.orquestador.infrastructure.webclient;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import bs.experian.orquestador.infrastructure.config.OrquestadorProperties;
import bs.experian.orquestador.infrastructure.dto.cutodia.CustodiaDocumentoRequest;
import bs.experian.orquestador.infrastructure.exceptions.WebclientErrorMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdenCustodiaDocumentoClient {
	
	private final WebClient webClient;
	private final OrquestadorProperties props;
	
	public void ordenarCustodia(EventoProcesadoDto evento) {
		
		String urlBaseCustodia = props.getApi().getCustodia().getBaseUrl();
		String urlCustodiaDoc = props.getApi().getCustodia().getCustodiaDocUrl();
		
		CustodiaDocumentoRequest request = new CustodiaDocumentoRequest (
				evento.getQueryId(),
				evento.getDocumento().getDocumentCode(),
				evento.getNotificationId());
				
		webClient.post()
			.uri(urlBaseCustodia + urlCustodiaDoc)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.retrieve()
			.onStatus(HttpStatusCode::isError,
				    resp -> WebclientErrorMapper.toAgoraException(resp, "Error llamando a custodia-experian para "
				    		+ "orden de cusstodia documentos"))
			.toBodilessEntity()
			.block();
	}

}
