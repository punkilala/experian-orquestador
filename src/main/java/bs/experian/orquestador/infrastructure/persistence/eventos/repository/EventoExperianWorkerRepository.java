package bs.experian.orquestador.infrastructure.persistence.eventos.repository;

import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import bs.experian.orquestador.infrastructure.persistence.eventos.entity.EventoExperianVivoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EventoExperianWorkerRepository  {
	
	private final JdbcTemplate template;
	
	/**
	 * Encolar evento para su procesamiento por worker
	 * @param queryId
	 * @param notificationId
	 * @param origenEvento
	 * @param eventType
	 * @param payloadJson
	 */
	@Transactional
	public void encolarEvento(EventoDto evento, String origenEvento, String payloadJson) {

	   template.update("""
		        INSERT INTO EVENTOS_EXPERIAN_VIVO (
		            QUERY_ID,
		            NOTIFICATION_ID,
		            ORIGEN_EVENTO,
		            EVENT_TYPE,
		            PAYLOAD_JSON,
		            ESTADO_TECNICO,
		            INTENTOS,
		            FECHA_ALTA
		        )
		        VALUES (?, ?, ?, ?, ?, 'PENDIENTE', 0, SYSTIMESTAMP)
		        """,
		        evento.getQueryId(),
		        evento.getNotificationId(),
		        origenEvento,
		        evento.getEventType(),
		        payloadJson
		    );
	}
	
	
	/**
	 * Buscar eventos sin procesar, o que hay que reintentar por un error anterior o eventos colgados mas de 15 minutos
	 * Si lo hay se marca como IN_PROGRESS para que no lo coja otro worker
	 * @return
	 */
	@Transactional
	public Optional<EventoExperianVivoEntity> reclamarEvento() {

		String sql = """
	            SELECT *
	            FROM EVENTOS_EXPERIAN_VIVO
	            WHERE ID_EVENTO IN (
	                SELECT ID_EVENTO
	                FROM (
	                    SELECT ID_EVENTO
	                    FROM EVENTOS_EXPERIAN_VIVO
	                    WHERE (
	                            (ESTADO_TECNICO = 'PENDIENTE'
	                             AND (NEXT_RETRY IS NULL OR NEXT_RETRY <= SYSTIMESTAMP))
	                         OR (ESTADO_TECNICO = 'IN_PROGRESS'
	                             AND PROCESO_DESDE < SYSTIMESTAMP - NUMTODSINTERVAL(15,'MINUTE'))
	                          )
	                    ORDER BY FECHA_ALTA
	                )
	                WHERE ROWNUM = 1
	            )
	            FOR UPDATE SKIP LOCKED
	            """;

	        return template.query(sql, rs -> {

	            if (!rs.next()) {
	                return Optional.empty();
	            }

	            EventoExperianVivoEntity evento = new EventoExperianVivoEntity();
	            evento.setId(rs.getLong("ID_EVENTO"));
	            evento.setQueryId(rs.getString("QUERY_ID"));
	            evento.setNotificationId(rs.getString("NOTIFICATION_ID"));
	            evento.setOrigenEvento(rs.getString("ORIGEN_EVENTO"));
	            evento.setEventType(rs.getString("EVENT_TYPE"));
	            evento.setPayloadJson(rs.getString("PAYLOAD_JSON"));

	            // Claim definitivo
	            template.update("""
	                UPDATE EVENTOS_EXPERIAN_VIVO
	                SET ESTADO_TECNICO = 'IN_PROGRESS',
	                    PROCESO_DESDE = SYSTIMESTAMP
	                WHERE ID_EVENTO = ?
	                """,
	                evento.getId()
	            );

	            return Optional.of(evento);
	        });
	}
	
	/**
	 * Guardar envento procesado en la tabla historica
	 * @param evento
	 * @param estadoExperian
	 * @param subestadoExperian
	 * @param documentCode
	 * @param errorEvento
	 * @param resultadoProceso
	 * @param errorCode
	 * @param errorMensaje
	 */
	public void finalizarEvento( EventoExperianVivoEntity evento, EventoProcesadoDto eventoProcesadoDto) {

	    // Insertar en histórico
	    template.update("""
	        INSERT INTO EVENTOS_EXPERIAN_HIST (
	            QUERY_ID,
	            NOTIFICATION_ID,
	            ORIGEN_EVENTO,
	            EVENT_TYPE,
	            ESTADO_EXPERIAN,
	            SUBESTADO_EXPERIAN,
	            DOCUMENT_CODE,
	            ERROR_EVENTO,
	            PAYLOAD_JSON,
	            FECHA_PROCESADO,
	            RESULTADO_PROCESO
	        )
	        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, SYSTIMESTAMP, 'OK')
	        """,
	        evento.getQueryId(),
	        evento.getNotificationId(),
	        evento.getOrigenEvento(),
	        evento.getEventType(),
	        eventoProcesadoDto.getEstadoExperian(),
	        eventoProcesadoDto.getSubestadoExperian(),
	        eventoProcesadoDto.getDocumento().getDocumentCode(),
	        null,
	        evento.getPayloadJson()
	    );

	    // Borrar de la cola de eventos a procesar
	    template.update("""
	        DELETE FROM EVENTOS_EXPERIAN_VIVO
	        WHERE ID_EVENTO = ?
	        """,
	        evento.getId()
	    );
	}
	
	/**
	 * Reprogarmar  para 1h si el evento no se ha podido procesar en su momemnto
	 * @param idEvento
	 * @param errorCode
	 * @param errorMensaje
	 */
	@Transactional
	public void reprogramarEvento(Long idEvento, String errorCode, String errorMensaje) {

		template.update("""
	            UPDATE EVENTOS_EXPERIAN_VIVO
	            SET ESTADO_TECNICO = 'PENDIENTE',
	                INTENTOS = INTENTOS + 1,
	                NEXT_RETRY = SYSTIMESTAMP + NUMTODSINTERVAL(1,'HOUR'),
	                PROCESO_DESDE = NULL,
	                ERROR_CODE = ?,
	                ERROR_MENSAJE = ?
	            WHERE ID_EVENTO = ?
	            """,
	            errorCode,
	            errorMensaje,
	            idEvento
		);
	}
}
