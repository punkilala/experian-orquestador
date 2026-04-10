package bs.experian.orquestador.infrastructure.persistence.documentos;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DOCUMENTOS_SOLICITUD_HIST")
@IdClass(DocumentosSolicitudPK.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentosSolicitudHistEntity extends DocumentosSolicitudBaseEntity {

    @Column(name = "FECHA_CIERRE", nullable = false)
    private OffsetDateTime fechaCierre;
    
    @PrePersist
    private void prePersist() {
        if (this.fechaCierre == null) {
            this.fechaCierre = OffsetDateTime.now();
        }
    }

}
