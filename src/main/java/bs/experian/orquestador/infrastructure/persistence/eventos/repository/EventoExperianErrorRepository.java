package bs.experian.orquestador.infrastructure.persistence.eventos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bs.experian.orquestador.infrastructure.persistence.eventos.entity.EventoExperianErrorEntity;

@Repository
public interface EventoExperianErrorRepository extends JpaRepository<EventoExperianErrorEntity, Long>{

}
