package bs.experian.orquestador.infrastructure.persistence.eventos.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "EVENTOS_EXPERIAN_VIVO")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventoExperianVivoEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_EVENTO")
	private Long id;
	@Column(name = "QUERY_ID", length = 60, nullable = false)
	private String queryId;
	@Column(name = "NOTIFICATION_ID", length = 80)
	private String notificationId;
	@Column(name = "ORIGEN_EVENTO", length = 20, nullable = false)
	private String origenEvento;
	@Column(name = "EVENT_TYPE", length = 50, nullable = false)
	private String eventType;
	@Lob
	@Column(name = "PAYLOAD_JSON", nullable = false)
	private String payloadJson;
	@Column(name = "ESTADO_TECNICO", length = 20, nullable = false)
	private String estadoTecnico;
	@Column(name = "INTENTOS", nullable = false)
	private Integer intentos;
	@Column(name = "NEXT_RETRY")
	private OffsetDateTime nextRetry;
	@Column(name = "PROCESO_DESDE")
	private OffsetDateTime	 procesoDesde;
	@Column(name = "FECHA_ALTA", nullable = false)
	private OffsetDateTime fechaAlta;
	@Column(name = "ERROR_CODE", length = 50)
	private String errorCode;
	@Column(name = "ERROR_MENSAJE", length = 4000)
	private String errorMensaje;
	
	

}
