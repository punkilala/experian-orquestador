package bs.experian.orquestador.infrastructure.persistence.eventos.entity;

import java.time.OffsetDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "EVENTOS_EXPERIAN_HIST")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventosExperianHistEntity {
	
	@Id
    @Column(name = "ID_EVENTO", nullable = false)
    private Long idEvento;

    @Column(name = "QUERY_ID", nullable = false, length = 60)
    private String queryId;

    @Column(name = "NOTIFICATION_ID", length = 80)
    private String notificationId;

    @Column(name = "ORIGEN_EVENTO", nullable = false, length = 20)
    private String origenEvento;

    @Column(name = "EVENT_TYPE", nullable = false, length = 100)
    private String eventType;

    @Column(name = "ESTADO_EXPERIAN", length = 100)
    private String estadoExperian;

    @Column(name = "SUBESTADO_EXPERIAN", length = 100)
    private String subestadoExperian;

    @Column(name = "DOCUMENT_CODE", length = 100)
    private String documentCode;

    @Lob
    @Column(name = "PAYLOAD_JSON", nullable = false)
    private String payloadJson;
    
    @Column(name = "FECHA_ALTA", nullable = false)
    private OffsetDateTime fechaAlta;
    
    @Column(name = "FECHA_PROCESADO", nullable = false)
    private OffsetDateTime fechaProcesado;

    @Column(name = "RESULTADO_PROCESO", nullable = false, length = 30)
    private String resultadoProceso;

    @Column(name = "ERROR_CODE", length = 50)
    private String errorCode;

    @Lob
    @Column(name = "ERROR_MENSAJE")
    private String errorMensaje;

}
