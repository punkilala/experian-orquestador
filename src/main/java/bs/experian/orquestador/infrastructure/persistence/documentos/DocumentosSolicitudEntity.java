package bs.experian.orquestador.infrastructure.persistence.documentos;


import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DynamicUpdate
@Entity
@Table(name = "DOCUMENTOS_SOLICITUD")
@IdClass(DocumentosSolicitudPK.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
public class DocumentosSolicitudEntity extends DocumentosSolicitudBaseEntity {

}
