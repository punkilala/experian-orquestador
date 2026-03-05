package bs.experian.orquestador.infrastructure.web.validations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = SolicitudNuevaValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSolicitudNueva {
	
	String message() default "Solicitud invalida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
	
}
