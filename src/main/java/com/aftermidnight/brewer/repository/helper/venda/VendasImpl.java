package com.aftermidnight.brewer.repository.helper.venda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Year;
import java.util.List;
import java.util.Optional;

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

import com.aftermidnight.brewer.dto.VendaMes;
import com.aftermidnight.brewer.dto.VendaMesOrigem;
import com.aftermidnight.brewer.model.StatusVenda;
import com.aftermidnight.brewer.model.TipoPessoa;
import com.aftermidnight.brewer.model.Venda;
import com.aftermidnight.brewer.repository.filter.VendaFilter;
import com.aftermidnight.brewer.repository.paginacao.PaginacaoUtil;

public class VendasImpl implements VendasQueries {


	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<Venda> filtrar(VendaFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}
	

	@Override
	@Transactional(readOnly = true)
	public Venda buscarComItens(Long codigo) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);
		criteria.createAlias("itens", "i", JoinType.LEFT_OUTER_JOIN)
			.add(Restrictions.eq("codigo", codigo))
			.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return (Venda) criteria.uniqueResult();
	}

	
	
	@Override
	public BigDecimal valorTotalNoAno() {
		String jpql = "select sum(valorTotal) from Venda where year(dataCriacao) = :ano and status = :status";
		Optional<BigDecimal> optional = Optional.ofNullable(
								manager.createQuery(jpql, BigDecimal.class)
										.setParameter("ano", Year.now().getValue())
										.setParameter("status", StatusVenda.EMITIDA)
										.getSingleResult()
				
								);
		
		return optional.orElse(BigDecimal.ZERO);
	}
	
	@Override
	public BigDecimal valorTotalNoMes() {
		String jpql = "select sum(valorTotal) from Venda where month(dataCriacao) = :mes and status = :status";
		Optional<BigDecimal> optional = Optional.ofNullable(
								manager.createQuery(jpql, BigDecimal.class)
										.setParameter("mes", MonthDay.now().getMonthValue())
										.setParameter("status", StatusVenda.EMITIDA)
										.getSingleResult()
								);
		
		return optional.orElse(BigDecimal.ZERO);
	}
	
	@Override
	public BigDecimal valorTicketMedio() {
		String jpql = "select sum(valorTotal)/count(*) from Venda where year(dataCriacao) = :ano and status = :status";
		Optional<BigDecimal> optional = Optional.ofNullable(
								manager.createQuery(jpql, BigDecimal.class)
										.setParameter("ano", Year.now().getValue())
										.setParameter("status", StatusVenda.EMITIDA)
										.getSingleResult()
				
								);
		
		return optional.orElse(BigDecimal.ZERO);
	}
	
	@Override
	public Long totalClientes() {
		String jpql = "select count(codigo) from Cliente";
		Optional<Long> optional = Optional.ofNullable(manager.createQuery(jpql, Long.class).getSingleResult());
		return optional.orElse(0L);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<VendaMes> totalPorMes(){
		List<VendaMes> vendasMes = manager.createNamedQuery("Vendas.totalEmitidaPorMes").getResultList();
		
		LocalDate hoje = LocalDate.now();
		for(int i = 1; i < 7 ; i++){
			String mesIdeal = String.format("%d/%02d", hoje.getYear(), hoje.getMonth().getValue());
			
			boolean possuiMes = vendasMes.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
			if(! possuiMes){
				vendasMes.add(i-1, new VendaMes(mesIdeal,0));
			}
			
			hoje = hoje.minusMonths(1);
			
		}
		
		return vendasMes;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<VendaMesOrigem> totalPorMesOrigem() {
		List<VendaMesOrigem> vendaMesOrigem = manager.createNamedQuery("Vendas.totalEmitidaPorMesOrigem").getResultList();
		
		LocalDate hoje = LocalDate.now();
		for(int i = 1; i < 7 ; i++){
			String mesIdeal = String.format("%d/%02d", hoje.getYear(), hoje.getMonth().getValue());
			
			boolean possuiMes = vendaMesOrigem.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
			if(! possuiMes){
				vendaMesOrigem.add(i-1, new VendaMesOrigem(mesIdeal,0,0));
			}
			
			hoje = hoje.minusMonths(1);
			
		}
		
		return vendaMesOrigem;
	}
	
	private Long total(VendaFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}
	
	private void adicionarFiltro(VendaFilter filtro, Criteria criteria) {
		criteria.createAlias("cliente", "c");
		
		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getCodigo())) {
				criteria.add(Restrictions.eq("codigo", filtro.getCodigo()));
			}
			
			if (filtro.getStatus() != null) {
				criteria.add(Restrictions.eq("status", filtro.getStatus()));
			}
			
			if (filtro.getDesde() != null) {
				LocalDateTime desde = LocalDateTime.of(filtro.getDesde(), LocalTime.of(0, 0));
				criteria.add(Restrictions.ge("dataCriacao", desde));
			}
			
			if (filtro.getAte() != null) {
				LocalDateTime ate = LocalDateTime.of(filtro.getAte(), LocalTime.of(23, 59));
				criteria.add(Restrictions.le("dataCriacao", ate));
			}
			
			if (filtro.getValorMinimo() != null) {
				criteria.add(Restrictions.ge("valorTotal", filtro.getValorMinimo()));
			}
			
			if (filtro.getValorMaximo() != null) {
				criteria.add(Restrictions.le("valorTotal", filtro.getValorMaximo()));
			}
			
			if (!StringUtils.isEmpty(filtro.getNomeCliente())) {
				criteria.add(Restrictions.ilike("c.nome", filtro.getNomeCliente(), MatchMode.ANYWHERE));
			}
			
			if (!StringUtils.isEmpty(filtro.getCpfOuCnpjCliente())) {
				criteria.add(Restrictions.eq("c.cpfOuCnpj", TipoPessoa.removerFormatacao(filtro.getCpfOuCnpjCliente())));
			}
		}
	}



}
