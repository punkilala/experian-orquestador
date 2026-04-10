package bs.experian.orquestador.application;

import org.springframework.stereotype.Service;

import bs.experian.orquestador.infrastructure.persistence.documentos.DocumentosSolicitudEntity;
import bs.experian.orquestador.infrastructure.persistence.documentos.DocumentosSolicitudHistEntity;
import bs.experian.orquestador.infrastructure.persistence.documentos.ProcesadorDocumentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentoApplicationService {
	
	private final ProcesadorDocumentoRepository procesadorDocumentoRepository;
	
	/**
	 * Obtener documentos de la tabla activa de la solicitud
	 * @param queryId
	 * @return
	 */
	public List<DocumentosSolicitudEntity> listatDocumentosTablaActiva (String queryId) {
		return procesadorDocumentoRepository.obtenerDocumentosSolicitud(queryId);
	}
	
	/**
	 * obtener todos los documentos hist de la solicitud
	 * @param queryId
	 * @return
	 */
	public List<DocumentosSolicitudHistEntity>listaDocumentosHistorico (String queryId){
		return procesadorDocumentoRepository.obtenerDocumentosHistSolicitud(queryId);
	}
}
