package bs.experian.orquestador.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "EVENTOS_EXPERIAN_ERROR")
public class EventoExperianErrorEntity {
	 	@Id
	    @Column(name = "ID_EVENTO", nullable = false)
	    private Long id;

	    @Column(name = "QUERY_ID", nullable = false, length = 60)
	    private String queryId;

	    @Column(name = "NOTIFICATION_ID", length = 100)
	    private String notificationId;

	    @Column(name = "ORIGEN_EVENTO", nullable = false, length = 50)
	    private String origenEvento;

	    @Column(name = "EVENT_TYPE", nullable = false, length = 50)
	    private String eventType;

	    @Column(name = "ESTADO_EXPERIAN", length = 50)
	    private String estadoExperian;

	    @Column(name = "SUBESTADO_EXPERIAN", length = 50)
	    private String subestadoExperian;

	    @Column(name = "DOCUMENT_CODE", length = 50)
	    private String documentCode;

	    @Lob
	    @Column(name = "PAYLOAD_JSON", nullable = false)
	    private String payloadJson;

	    @Column(name = "ERROR_CODE", nullable = false, length = 50)
	    private String errorCode;

	    @Column(name = "ERROR_MENSAJE", length = 500)
	    private String errorMensaje;

	    // TIMESTAMP WITH TIME ZONE
	    @Column(name = "FECHA_ERROR_FINAL", nullable = false)
	    private OffsetDateTime fechaErrorFinal;

}
