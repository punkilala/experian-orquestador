package bs.experian.orquestador.infrastructure.persistence.eventos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoExperianErrorRepository extends JpaRepository<EventoExperianErrorEntity, Long>{

}
