package com.aftermidnight.brewer.repository.helper.cidade;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aftermidnight.brewer.model.Cidade;
import com.aftermidnight.brewer.repository.filter.CidadeFilter;
import com.aftermidnight.brewer.repository.paginacao.PaginacaoUtil;

public class CidadesImpl implements CidadesQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<Cidade> filtrar(CidadeFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cidade.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		
		//para evitar problemas do lazy load deve-se inicializar o cidade.estado
		criteria.createAlias("estado", "e", JoinType.LEFT_OUTER_JOIN);
		
		
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}

	private Long total(CidadeFilter filtro){
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cidade.class);
		criteria.setProjection(Projections.rowCount());
		adicionarFiltro(filtro, criteria);
		return (Long)criteria.uniqueResult();
	}

	private void adicionarFiltro(CidadeFilter filtro, Criteria criteria) {
		if(filtro != null){
			
			if(!StringUtils.isEmpty(filtro.getNome())){
				criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}
			
			if(!StringUtils.isEmpty(filtro.getEstado())){
				criteria.add(Restrictions.eq("estado", filtro.getEstado()));
			}

		}
	}

	@Override
	@Transactional(readOnly = true)
	public Cidade buscarComEstado(Long codigo) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cidade.class);
		criteria.createAlias("estado", "e", JoinType.LEFT_OUTER_JOIN)
			.add(Restrictions.eq("codigo", codigo))
			.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return (Cidade) criteria.uniqueResult();
	}


}
