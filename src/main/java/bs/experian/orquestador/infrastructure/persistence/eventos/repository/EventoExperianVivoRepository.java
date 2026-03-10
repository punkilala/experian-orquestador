package bs.experian.orquestador.infrastructure.persistence.eventos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bs.experian.orquestador.infrastructure.persistence.eventos.entity.EventoExperianVivoEntity;

public interface EventoExperianVivoRepository extends JpaRepository<EventoExperianVivoEntity, Long> {

}
