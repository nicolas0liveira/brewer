package com.aftermidnight.brewer.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

import com.aftermidnight.brewer.validation.validator.ConfirmarAtributosIguaisValidator;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { ConfirmarAtributosIguaisValidator.class })
public @interface ConfirmarAtributosIguais {

	//sobrescreve o atributo message do @Pattern
	@OverridesAttribute(constraint = Pattern.class, name = "message")
	String message() default "Atributos não conferem";
		
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	
	String atributo();
	String atributoConfirmacao();
	
	
}
