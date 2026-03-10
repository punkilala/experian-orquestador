package bs.experian.orquestador.infrastructure.persistence.eventos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bs.experian.orquestador.infrastructure.persistence.eventos.entity.EventosExperianHistEntity;

@Repository
public interface EventoExperianHistRepository extends JpaRepository<EventosExperianHistEntity, Long> {

}
