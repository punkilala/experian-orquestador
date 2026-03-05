package bs.experian.orquestador.infrastructure.persistence.eventos;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoExperianVivoRepository extends JpaRepository<EventoExperianVivoEntity, Long> {

}
