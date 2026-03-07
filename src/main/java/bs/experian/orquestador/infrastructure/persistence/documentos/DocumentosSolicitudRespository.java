package bs.experian.orquestador.infrastructure.persistence.documentos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentosSolicitudRespository extends JpaRepository<DocumentosSolicitudEntity, DocumentosSolicitudPK>{
	Optional<DocumentosSolicitudEntity>findByQueryIdAndDocumentCode(String queryId, String documentCode);
	
}
