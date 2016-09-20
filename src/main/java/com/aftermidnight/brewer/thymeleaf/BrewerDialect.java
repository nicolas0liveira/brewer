package com.aftermidnight.brewer.thymeleaf;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import com.aftermidnight.brewer.thymeleaf.processor.ActiveMenuForAttributeTagProcessor;
import com.aftermidnight.brewer.thymeleaf.processor.CabecalhoOrdenavelElementTagProcessor;
import com.aftermidnight.brewer.thymeleaf.processor.ClassErrorForAttributeTagProcessor;
import com.aftermidnight.brewer.thymeleaf.processor.MessageElementTagProcessor;
import com.aftermidnight.brewer.thymeleaf.processor.PaginacaoElementTagProcessor;

public class BrewerDialect extends AbstractProcessorDialect {

	public BrewerDialect() {
		super("AfterMidnight Brewer", "brewer", StandardDialect.PROCESSOR_PRECEDENCE);
	}

	@Override
	public Set<IProcessor> getProcessors(String dialectPrefix) {
		final Set<IProcessor> processadores = new HashSet<>();
		processadores.add(new ClassErrorForAttributeTagProcessor(dialectPrefix));
		processadores.add(new MessageElementTagProcessor(dialectPrefix));
		processadores.add(new CabecalhoOrdenavelElementTagProcessor(dialectPrefix));
		processadores.add(new PaginacaoElementTagProcessor(dialectPrefix));
		processadores.add(new ActiveMenuForAttributeTagProcessor(dialectPrefix));
		
		return processadores;
	}

}
