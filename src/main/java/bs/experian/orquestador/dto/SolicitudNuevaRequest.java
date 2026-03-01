package bs.experian.orquestador.dto;


import bs.experian.orquestador.utils.DomainEnum;
import bs.experian.orquestador.validations.ValidSolicitudNueva;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidSolicitudNueva
public class SolicitudNuevaRequest {
	/////////// COMUNES
	@NotBlank
    private String phoneNumber;
	@NotNull
    private Integer belenderCircuit;
	@NotBlank
    private String documentationPack;
	
	///////////	PF 
	//noAutonomo= 00 autonomo=01
    private String personType; 
    //idfiscal PF o Representante PJ
    @NotBlank
    private String personId;
    @NotBlank
    private String firstName;
    @NotBlank
    private String firstSurname;
    @NotBlank
    private String secondSurname;
    private String birthDate;
    private String supportNumber;
    private String personIdExpirationDate;
    
    
    ////////////PJ
    private String email;
    private String personIdFrontPhoto;
    private String personIdBackPhoto;
    private String companyId;
    private String companyName;
    
    
    /////////// SOLO ORQUESTADOR
	@NotNull
	private DomainEnum.TipoPersona personCategory;
	@NotBlank
	private String OfficeCode;
	@NotBlank
	private String usernameGestor;
	@NotBlank
	private String origin;


    
   
    
    
    
 

    

}
	
	
