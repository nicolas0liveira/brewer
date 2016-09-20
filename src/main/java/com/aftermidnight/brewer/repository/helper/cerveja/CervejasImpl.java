package com.aftermidnight.brewer.repository.helper.cerveja;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aftermidnight.brewer.dto.CervejaDTO;
import com.aftermidnight.brewer.dto.ValorItensEstoque;
import com.aftermidnight.brewer.model.Cerveja;
import com.aftermidnight.brewer.model.Estilo;
import com.aftermidnight.brewer.repository.filter.CervejaFilter;
import com.aftermidnight.brewer.repository.paginacao.PaginacaoUtil;
import com.aftermidnight.brewer.storage.FotoStorage;

public class CervejasImpl implements CervejasQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@Autowired
	private FotoStorage fotoStorage;
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<Cerveja> filtrar(CervejaFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cerveja.class);
		
		paginacaoUtil.preparar(criteria, pageable);
		
		adicionarFiltro(filtro, criteria);
		
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}
	
	@Override
	public ValorItensEstoque valorItensEstoque() {
		String query = "select new com.aftermidnight.brewer.dto.ValorItensEstoque(sum(valor * quantidadeEstoque), sum(quantidadeEstoque)) from Cerveja";
		return manager.createQuery(query, ValorItensEstoque.class).getSingleResult();
	}

	private Long total(CervejaFilter filtro){
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cerveja.class);
		criteria.setProjection(Projections.rowCount());
		adicionarFiltro(filtro, criteria);
		return (Long)criteria.uniqueResult();
	}

	private void adicionarFiltro(CervejaFilter filtro, Criteria criteria) {
		if(filtro != null){
			if(!StringUtils.isEmpty(filtro.getSku())){
				criteria.add(Restrictions.eq("sku", filtro.getSku()));
			}
			
			if(!StringUtils.isEmpty(filtro.getNome())){
				criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}
			
			if(isEstiloPresente(filtro.getEstilo())){
				criteria.add(Restrictions.eq("estilo", filtro.getEstilo()));
			}
			
			if(!StringUtils.isEmpty(filtro.getOrigem())){
				criteria.add(Restrictions.eq("origem", filtro.getOrigem()));
			}
			
			if(!StringUtils.isEmpty(filtro.getSabor())){
				criteria.add(Restrictions.eq("sabor", filtro.getSabor()));
			}
			
			if(!StringUtils.isEmpty(filtro.getValorDe())){
				criteria.add(Restrictions.ge("valor", filtro.getValorDe()));
			}
			
			if(!StringUtils.isEmpty(filtro.getValorAte())){
				criteria.add(Restrictions.le("valor", filtro.getValorAte()));
			}
			
		}
	}
	

	@Override
	public List<CervejaDTO> porSkuOuNome(String skuOuNome) {
		String jpql = "SELECT new com.aftermidnight.brewer.dto.CervejaDTO(codigo, sku, nome, origem, valor, foto) "
				+ " FROM Cerveja WHERE lower(sku) like lower(:skuOuNome) OR lower(nome) like lower(:skuOuNome) ";
		
		List<CervejaDTO> cervejasFiltradas = manager.createQuery(jpql, CervejaDTO.class)
				.setParameter("skuOuNome", skuOuNome + "%")
				.getResultList();

		cervejasFiltradas.forEach(c -> c.setUrlFotoThumbnail(fotoStorage.getUrlThumbnail(c.getFoto())));

		return cervejasFiltradas;
	}
	
	
	
	private boolean isEstiloPresente(Estilo estilo){
		return (estilo != null && estilo.getCodigo() != null);
		
	}


}
