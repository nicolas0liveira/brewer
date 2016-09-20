package com.aftermidnight.brewer.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import com.aftermidnight.brewer.model.Grupo;

public class GrupoConverter implements Converter<String, Grupo> {

	@Override
	public Grupo convert(String source) {
		Grupo obj = new Grupo();
		
		if(StringUtils.isEmpty(source)) return null;
		
		obj.setCodigo(Long.valueOf(source));
		return obj;
	}



}
