package bs.experian.orquestador.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import bs.experian.orquestador.infrastructure.persistence.documentos.ProcesadorDocumentoRepository;
import bs.experian.orquestador.infrastructure.webclient.OrdenDescargaDocumentoClient;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentosApplicagtionService {
	
	private final ProcesadorDocumentoRepository procesadorDocumentoRepository;
	private final OrdenDescargaDocumentoClient ordenDescargaDocumentoClient;
	
	/**
	 * Experian notifica que un documento esta disponible
	 * se registra como pte descarga
	 * @param dto
	 */
	
	public void registrarDocumentoPteDescarga (EventoProcesadoDto dto) {
		//registrar en bdd
		procesadorDocumentoRepository.registrarDocumentoPteDescarga(dto);
		//llamar a integracion para que lo descarge
		ordenDescargaDocumentoClient.ordenarDescarga(dto);
	
	}
	

	/**
	 * Pasar a historico un documento que experian dice que no lo ha obtenido
	 * @param dto
	 */
	@Transactional
	public void registrarDocEnHistExperianNoObtenido(EventoProcesadoDto dto) {
		procesadorDocumentoRepository.registrarDocEnHistExperianNoObtenido(dto);
	}

}
