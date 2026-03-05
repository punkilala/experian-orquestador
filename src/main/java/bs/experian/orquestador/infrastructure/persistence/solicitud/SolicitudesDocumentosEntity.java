package bs.experian.orquestador.infrastructure.persistence.solicitud;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SOLICITUDES_DOCUMENTOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudesDocumentosEntity {
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

    @Column(name = "FECHA_ULTIMA_ACT")
    private OffsetDateTime fechaUltimaActualizacion;

    @PrePersist
    public void prePersist() {
        if (fechaAlta == null) {
            fechaAlta = OffsetDateTime.now();
        }
    }

}
