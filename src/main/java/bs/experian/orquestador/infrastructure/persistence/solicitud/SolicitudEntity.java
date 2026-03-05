package bs.experian.orquestador.infrastructure.persistence.solicitud;



import java.time.LocalDateTime;

import bs.experian.orquestador.domain.enums.DomainEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="solicitudes_experian")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudEntity {
	//referencia solicitud Experian
	@Id
	@Column(name = "QUERY_ID", nullable = false)	
	private String queryId ;
	//referencia solicitud interna creada por bs
	@Column(name = "REQUEST_REFERENCE", nullable = false, unique = true)
	private String  requestReference ;
	
	@Column(name = "FECHA_CREACION")
	private LocalDateTime fechaCreacion;
	
	@Column(name = "FECHA_ULTIMA_ACTUALIZACION")
	private LocalDateTime fechaUltimaActualizacion;

	
	@Column(name = "ESTADO_EXPERIAN", nullable = false)
	private String estadoExperian;
	
	@Column(name = "SUBESTADO_EXPERIAN", nullable = false)
	private String subEstadoExperian;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "ESTADO_INTERNO", nullable = false)
	private DomainEnum.EstadoInterno estadoInterno;
	

	@Column(name = "OFICINA_GESTOR")
	private String oficinaGestor;
	
	@Column(name = "USER_NAME_GESTOR")
	private String userNameGestor;
	
	
	
	@Enumerated(EnumType.STRING)
	@Column(name = "PERSON_CATEGORY", length = 2, nullable = false)
	private DomainEnum.TipoPersona personCategoy;
	
	//id fiscal cliente
	@Column(name = "PERSON_ID", nullable = false)
	private String personId;
	
	@Column(name = "CIF_EMPRESA")
	private String companyId;
	
	
	
	//numero del pack documental
	@Column(name = "PACK_DOCUMENTAL", nullable = false)
	private String packDocumental;
	@Column(name = "ORIGEN", nullable = false)
	private String origin;
	
	
	
	
	
	
	
	

}
