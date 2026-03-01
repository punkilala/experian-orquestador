package bs.experian.orquestador.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bs.experian.orquestador.entity.SolicitudEntity;

@Repository
public interface SolicitudRepository extends JpaRepository<SolicitudEntity, String> {
	

}
