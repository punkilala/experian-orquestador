package bs.experian.orquestador.infrastructure.persistence.solicitud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudesDocumentosRespository extends JpaRepository<SolicitudesDocumentosEntity, Long>{

}
