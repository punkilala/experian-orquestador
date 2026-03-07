package bs.experian.orquestador.infrastructure.persistence.documentos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentosSolicitudHistRepository extends JpaRepository<DocumentosSolicitudHistEntity, DocumentosSolicitudHistPK>{
	Optional<DocumentosSolicitudHistRepository>findByQueryIdAndDocumentCode(String queryId, String documentCode);
}
