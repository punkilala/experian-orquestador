package bs.experian.orquestador.infrastructure.persistence.solicitud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudRepository extends JpaRepository<SolicitudEntity, String> {
	

}
