package bs.experian.orquestador.infrastructure.persistence.solicitud;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface SolicitudRepository extends JpaRepository<SolicitudEntity, String> {
	/**
	 * buscar solicitudes finalizadas hace 90 días
	 * @param estado
	 * @param subestados
	 * @param fechaLimite
	 * @param personId
	 * @return
	 */
	@Query("""
			   SELECT s
			   FROM SolicitudEntity s
			   WHERE s.estadoExperian = :estado
			     AND s.subEstadoExperian IN :subestados
			     AND s.fechaCreacion >= :fechaLimite
			     AND s.personId = :personId
			""")
	List<SolicitudEntity> findSolicitudesVigentes(
			@Param("estado") String estado,
			@Param("subestados") List<String> subestados, 
			@Param("fechaLimite") OffsetDateTime fechaLimite,
			@Param("personId") String personId);
	
	
	/**
	 * buscar solicitudes en curso
	 * @param estadoExperian
	 * @param personId
	 * @return
	 */
	List<SolicitudEntity> findByEstadoExperianInAndPersonId(List<String> estadoExperian,String personId);

}
