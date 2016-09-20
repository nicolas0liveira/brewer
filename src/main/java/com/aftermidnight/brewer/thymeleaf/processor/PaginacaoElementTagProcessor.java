package com.aftermidnight.brewer.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

public class PaginacaoElementTagProcessor extends AbstractElementTagProcessor {
	private static final String NOME_TAG = "paginacao";
	private static final int PRECEDENCIA = 1000;
	
	public PaginacaoElementTagProcessor(String dialectPrefix) {
		super( TemplateMode.HTML, dialectPrefix, NOME_TAG, true, null, false, PRECEDENCIA );
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
			IElementTagStructureHandler structureHandler) {

		IModelFactory modelFactory = context.getModelFactory();
		IModel model = modelFactory.createModel();

		IAttribute attrPagina = tag.getAttribute("pagina");
		

		//<th:block th:replace="fragments/Tabela :: paginacao (${pagina})"></th:block>
		IStandaloneElementTag tagFragment = modelFactory.createStandaloneElementTag("th:block",
				"th:replace",
				String.format("fragments/Tabela :: paginacao (%s)",attrPagina.getValue()));
	
		model.add(tagFragment);
		
		boolean aindaPrecisaSerProcessadoPeloThymeleaf = true;//pois tem o th na tag substituída.
		structureHandler.replaceWith(model, aindaPrecisaSerProcessadoPeloThymeleaf);
		
	}

}
