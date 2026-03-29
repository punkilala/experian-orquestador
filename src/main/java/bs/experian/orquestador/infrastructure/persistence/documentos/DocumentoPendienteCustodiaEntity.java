package bs.experian.orquestador.infrastructure.persistence.documentos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "DOCUMENTOS_PENDIENTES_CUSTODIA")
@IdClass(DocumentoPendienteCustodiaPK.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoPendienteCustodiaEntity {

    @Id
    @Column(name = "QUERY_ID", nullable = false, length = 60)
    private String queryId;

    @Id
    @Column(name = "DOCUMENT_CODE", nullable = false, length = 100)
    private String documentCode;

    @Column(name = "NOTIFICATION_ID", length = 100)
    private String notificationId;

    @Lob
    @Column(name = "PDF_DOCUMENT")
    private byte[] pdfDocument;

    @Lob
    @Column(name = "JSON_DOCUMENT")
    private String jsonDocument;
    
    @Column(name = "ESTATUS", length = 10)
    private String estatus;
    
    @Column(name = "INTENTOS", nullable = false)
    private Integer intentos;

    @Column(name = "FECHA_ALTA", nullable = false)
    private OffsetDateTime fechaAlta;
}