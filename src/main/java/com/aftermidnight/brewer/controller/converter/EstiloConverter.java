package com.aftermidnight.brewer.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import com.aftermidnight.brewer.model.Estilo;

public class EstiloConverter implements Converter<String, Estilo> {

	@Override
	public Estilo convert(String source) {
		Estilo estilo = new Estilo();
		
		if(StringUtils.isEmpty(source)) return null;
		
		estilo.setCodigo(Long.valueOf(source));
		return estilo;
	}



}
