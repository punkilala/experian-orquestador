package bs.experian.orquestador.infrastructure.persistence.documentos;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DOCUMENTOS_SOLICITUD_HIST")
@IdClass(DocumentosSolicitudHistPK.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentosSolicitudHistEntity {

    @Id
    @Column(name = "QUERY_ID", length = 60, nullable = false)
    private String queryId;

    @Id
    @Column(name = "DOCUMENT_CODE", length = 100, nullable = false)
    private String documentCode;
    
    @Column(name = "NOTIFICATION_ID", length = 100, nullable = false)
    private String notificationId;

    @Column(name = "ESTADO_DOCUMENTO", length = 30, nullable = false)
    private String estadoDocumento;

    @Lob
    @Column(name = "DOCUMENT_JSON")
    private String documentJson;
    
    @Column(name = "DOCUMENT_PDF" , length = 20)
    private String documentPdf;

    @Column(name = "FECHA_ALTA", nullable = false)
    private OffsetDateTime fechaAlta;
    
    @Column(name = "FECHA_ULTIMA_ACT")
    private OffsetDateTime fechaUltimaActualizacion;

    @Column(name = "FECHA_CIERRE", nullable = false)
    private OffsetDateTime fechaCierre;
    
    @PrePersist
    private void prePersist() {
        if (this.fechaCierre == null) {
            this.fechaCierre = OffsetDateTime.now();
        }
    }

}
