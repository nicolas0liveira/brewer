package com.aftermidnight.brewer.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Pattern(regexp = "([a-zA-Z]{2}\\d{4})?") //a ? diz que o pattern é opcional, e apenas será aplicado caso exista valor.
public @interface SKU {

	//sobrescreve o atributo message do @Pattern
	@OverridesAttribute(constraint = Pattern.class, name = "message")
//	String message() default "SKU deve seguir o padrão XX9999";
	String message() default "{com.aftermidnight.constraints.SKU.message}"; //chave do message.properties
	
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	
}