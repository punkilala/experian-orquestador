package bs.experian.orquestador.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bs.experian.orquestador.entity.EventoExperianErrorEntity;

@Repository
public interface EventoExperianErrorRepository extends JpaRepository<EventoExperianErrorEntity, Long>{

}
