package bs.experian.orquestador.infrastructure.web.validations;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import bs.experian.orquestador.domain.enums.DomainEnum;
import bs.experian.orquestador.infrastructure.dto.integracion.SolicitudNuevaRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SolicitudNuevaValidator implements ConstraintValidator<ValidSolicitudNueva, SolicitudNuevaRequest> {

	@Override
	public boolean isValid(SolicitudNuevaRequest value, ConstraintValidatorContext context) {
		if (value == null) {
            return true;
        }
        boolean valid = true;
        
        context.disableDefaultConstraintViolation();
        
		if (esPersonaFisica(value)) {
            // =====VALIDACIONES PF =====
			valid &= validarPersonType(value, context);
			valid &= validarFecha (value.getBirthDate(), "birthDate", context);
			if(value.getPersonId() != null &&value.getPersonId().toUpperCase().matches("^\\d{8}[A-Z]$")) {
				valid &= validarNoNulo(value.getPersonIdExpirationDate(), "personIdExpirationDate", context);
			}else {
				valid &= validarNoNulo(value.getSupportNumber(), "supportNumber", context);
			}
			
		}else if (esPersonaJuridica(value)) {
			// =====VALIDACIONES PJ =====
			valid &= validarNoNulo (value.getEmail(), "email", context);
			valid &= validarNoNulo (value.getPersonIdFrontPhoto(), "personIdFrontPhoto", context);
			valid &= validarNoNulo (value.getPersonIdBackPhoto(), "personIdBackPhoto", context);
			valid &= validarNoNulo (value.getCompanyId(), "companyId", context);
			valid &= validarNoNulo (value.getCompanyName(), "companyName", context);
		}
		return valid;
	}
	
	private boolean esPersonaFisica(SolicitudNuevaRequest value) {
	    return value.getPersonCategory() == DomainEnum.TipoPersona.PF;
	}
	private boolean esPersonaJuridica(SolicitudNuevaRequest value) {
	    return value.getPersonCategory() == DomainEnum.TipoPersona.PJ;
	}
	
	//validar personType
	private boolean validarPersonType(SolicitudNuevaRequest value,  ConstraintValidatorContext context) {

	    String personType = value.getPersonType();

	    if (personType == null || personType.isBlank()) {
	        addError(context, "personType",
	                "personType es obligatorio para persona fisica");
	        return false;
	    }

	    if (!personType.equals("00") && !personType.equals("01")) {
	        addError(context, "personType",
	                "personType debe ser '00' o '01'");
	        return false;
	    }

	    return true;
	}
	
	//validar fecha y formato yyyy-MM-dd
	 public boolean validarFecha(String value, String campo, ConstraintValidatorContext context) {

	        if (value == null || value.trim().isEmpty()) {
	            addError(context, campo,
	            		campo + " campo requerido");
	            return false;
	        }

	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        sdf.setLenient(false);

	        try {
	            sdf.parse(value);
	            return true;
	        } catch (ParseException e) {
	        	addError(context, campo,
	            		campo + " el formato correcto es yyyy-MM-dd");
	            return false;
	        }
	    }
	 
	 public boolean validarNoNulo(String value, String campo, ConstraintValidatorContext context) {
		 boolean result = true;
		 if (value == null || value.trim().isEmpty()) {
			 addError(context, campo,
	            		campo + " campo requerido");
			 result = false;
		 }
		 
		 return result;
	 }
	
	private void addError(ConstraintValidatorContext context, String field, String message) {
		context.buildConstraintViolationWithTemplate(message)
		  .addPropertyNode(field)
		  .addConstraintViolation();
	}

}
