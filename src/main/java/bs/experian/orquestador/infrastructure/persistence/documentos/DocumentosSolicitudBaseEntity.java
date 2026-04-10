package bs.experian.orquestador.infrastructure.persistence.documentos;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class DocumentosSolicitudBaseEntity {
	@Column(name = "QUERY_ID", nullable = false, length = 60)
	@Id
    private String queryId;

    @Column(name = "DOCUMENT_CODE", nullable = false, length = 100)
    @Id
    private String documentCode;

    @Column(name = "NOTIFICATION_ID", nullable = false, length = 100)
    private String notificationId;

    @Column(name = "ESTADO_DOCUMENTO", length = 30)
    private String estadoDocumento;

    @Lob
    @Column(name = "DOCUMENT_JSON")
    private String documentJson;

    @Column(name = "DOCUMENT_PDF", length = 20)
    private String documentPdf;

    @Column(name = "FECHA_ALTA", nullable = false)
    private OffsetDateTime fechaAlta;

    @Column(name = "FECHA_ULTIMA_ACT")
    private OffsetDateTime fechaUltimaAct;
}
