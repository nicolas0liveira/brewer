package com.aftermidnight.brewer.validation.validator;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.apache.commons.beanutils.BeanUtils;

import com.aftermidnight.brewer.validation.ConfirmarAtributosIguais;

public class ConfirmarAtributosIguaisValidator implements ConstraintValidator<ConfirmarAtributosIguais, Object>{

	private String atributo;

	private String atributoConfirmacao;
	
	@Override
	public void initialize(ConfirmarAtributosIguais constraintAnnotation) {
		this.atributo =  constraintAnnotation.atributo();
		this.atributoConfirmacao = constraintAnnotation.atributoConfirmacao();
		
	}

	@Override
	public boolean isValid(Object object, ConstraintValidatorContext context) {
		boolean valido = false;
		try {
			Object valorAtributo = BeanUtils.getProperty(object, this.atributo);
			Object valorAtributoConfirmacao = BeanUtils.getProperty(object, this.atributoConfirmacao);
			
			valido = ambosSaoNull(valorAtributo, valorAtributoConfirmacao) 
					||	ambosSaoIguais(valorAtributo, valorAtributoConfirmacao);
			
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException("Erro ao recuperar valores dos atributos", e);
		}
		
		if(!valido){
			//para não duplicar a validação, apenas executar a nossa...
			context.disableDefaultConstraintViolation();
			
			String messageTemplate = context.getDefaultConstraintMessageTemplate();
			ConstraintViolationBuilder violationBuilder = context.buildConstraintViolationWithTemplate(messageTemplate);
			violationBuilder.addPropertyNode(atributoConfirmacao).addConstraintViolation();
		}
		
		return valido;
	}

	private boolean ambosSaoIguais(Object valorAtributo, Object valorAtributoConfirmacao) {
		return valorAtributo !=null && valorAtributo.equals(valorAtributoConfirmacao);
	}

	private boolean ambosSaoNull(Object valorAtributo, Object valorAtributoConfirmacao) {
		return valorAtributo == null && valorAtributoConfirmacao == null;
	}


}
