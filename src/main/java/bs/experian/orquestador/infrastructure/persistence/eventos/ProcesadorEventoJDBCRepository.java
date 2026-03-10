package bs.experian.orquestador.infrastructure.persistence.eventos;


import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import bs.experian.orquestador.infrastructure.persistence.eventos.entity.EventoExperianVivoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProcesadorEventoJDBCRepository {

	private final JdbcTemplate template;

	/**
	 * Buscar eventos sin procesar, o que hay que reintentar por un error anterior o eventos colgados mas de 15 minutos
	 * Si lo hay se marca como IN_PROGRESS para que no lo coja otro worker
	 * @return
	 */
	public Optional<EventoExperianVivoEntity> reclamarEvento() {
		
		
		OffsetDateTime  ahora = OffsetDateTime.now();
		OffsetDateTime limiteProceso = ahora.minusMinutes(15);
		
		String sqlUpdate = """
			    UPDATE EVENTOS_EXPERIAN_VIVO
			    SET ESTADO_TECNICO = 'IN_PROGRESS',
			        PROCESO_DESDE = ?
			    WHERE ID_EVENTO = ?
			""";

		String sqlBloqueo = """
				     SELECT *
				     FROM EVENTOS_EXPERIAN_VIVO
				     WHERE ID_EVENTO IN (
				SELECT ID_EVENTO
				         FROM (
				             SELECT ID_EVENTO
				             FROM EVENTOS_EXPERIAN_VIVO
				             WHERE (
				                     (ESTADO_TECNICO = 'PENDIENTE'
				                      AND (NEXT_RETRY IS NULL OR NEXT_RETRY <= ?))
				                  OR (ESTADO_TECNICO = 'IN_PROGRESS'
				                      AND PROCESO_DESDE < ?)
				                   )
				             ORDER BY FECHA_ALTA
				         )
				         WHERE ROWNUM = 1
				     )
				     FOR UPDATE SKIP LOCKED
				     """;
		//obtener evento a procesar
		return template.query(sqlBloqueo, 
				ps -> {
		            ps.setObject(1, ahora);
		            ps.setObject(2, limiteProceso);
		        },
				rs -> {

				if (!rs.next()) {
					return Optional.empty();
				}

			EventoExperianVivoEntity evento = new EventoExperianVivoEntity();
			evento.setId(rs.getLong("ID_EVENTO"));
			evento.setQueryId(rs.getString("QUERY_ID"));
			evento.setNotificationId(rs.getString("NOTIFICATION_ID"));
			evento.setOrigenEvento(rs.getString("ORIGEN_EVENTO"));
			evento.setEventType(rs.getString("EVENT_TYPE"));
			evento.setFechaAlta(rs.getObject("FECHA_ALTA", OffsetDateTime.class));
			evento.setPayloadJson(rs.getString("PAYLOAD_JSON"));

			// actualiar evento como en proceso
			template.update(sqlUpdate, ahora, evento.getId());

			return Optional.of(evento);
		});
	}
	
	

}
