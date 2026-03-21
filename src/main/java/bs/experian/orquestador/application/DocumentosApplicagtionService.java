package bs.experian.orquestador.application;

import org.springframework.stereotype.Service;

import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import bs.experian.orquestador.infrastructure.persistence.documentos.ProcesadorDocumentoRepository;
import bs.experian.orquestador.infrastructure.webclient.OrdenCustodiaDocumentoClient;
import bs.experian.orquestador.infrastructure.webclient.OrdenDescargaDocumentoClient;
import lombok.RequiredArgsConstructor;
import static bs.experian.orquestador.domain.constants.ExperianConstants.*;

@Service
@RequiredArgsConstructor
public class DocumentosApplicagtionService {
	
	private final ProcesadorDocumentoRepository procesadorDocumentoRepository;
	private final OrdenDescargaDocumentoClient ordenDescargaDocumentoClient;
	private final OrdenCustodiaDocumentoClient ordenCustodiaDocumentoClient;
	
	/**
	 * Experian notifica que un documento esta disponible
	 * se registra como pte descarga
	 * @param dto
	 */
	
	public void registrarDocumentoPteDescarga (EventoProcesadoDto dto) {
		//registrar en bdd
		procesadorDocumentoRepository.registrarDocumentoPteDescarga(dto);
		//llamar a integracion para que lo descarge{
		ordenDescargaDocumentoClient.ordenarDescarga(dto);
		
	
	}
	

	/**
	 * Pasar a historico un documento que experian dice que no lo ha obtenido
	 * @param dto
	 */
	public void registrarDocEnHistExperianNoObtenido(EventoProcesadoDto dto) {
		procesadorDocumentoRepository.registrarDocEnHistExperianNoObtenido(dto);
	}
	
	
	/**
	 * actualizar el estado del documento descargado/custodiado
	 * @param dto
	 */
	public void actualizarEstadoDocumento (EventoProcesadoDto dto) {
		if(null != dto.getDocumento() && DOC_PTE_CUSTODIA.equals(dto.getDocumento().getPdfDocument())){
			//llamar para custodiar
			ordenCustodiaDocumentoClient.ordenarCustodia(dto);
		}
		procesadorDocumentoRepository.actualizarEstadoDocumento(dto);
	}
	
}
