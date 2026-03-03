package bs.experian.orquestador.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bs.experian.orquestador.entity.SolicitudesDocumentosEntity;

@Repository
public interface SolicitudesDocumentosRespository extends JpaRepository<SolicitudesDocumentosEntity, Long>{

}
