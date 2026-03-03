package bs.experian.orquestador.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SOLICITUDES_DOCUMENTOS_HIST")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudesDocumentosHistEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DOCUMENTO")
    private Long id;

    @Column(name = "QUERY_ID", nullable = false, length = 60)
    private String queryId;

    @Column(name = "DOCUMENT_CODE", nullable = false, length = 100)
    private String documentCode;

    @Column(name = "ESTADO_DOCUMENTO", nullable = false, length = 30)
    private String estadoDocumento;

    @Lob
    @Column(name = "DOCUMENT_JSON")
    private String documentJson;

    @Column(name = "FECHA_ALTA", nullable = false)
    private OffsetDateTime fechaAlta;

    @Column(name = "FECHA_CIERRE", nullable = false)
    private OffsetDateTime fechaCierre;

    @PrePersist
    public void prePersist() {
        if (fechaCierre == null) {
            fechaCierre = OffsetDateTime.now();
        }
    }

}
