package com.aftermidnight.brewer.repository.helper.usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aftermidnight.brewer.model.Grupo;
import com.aftermidnight.brewer.model.Usuario;
import com.aftermidnight.brewer.model.UsuarioGrupo;
import com.aftermidnight.brewer.repository.filter.UsuarioFilter;
import com.aftermidnight.brewer.repository.paginacao.PaginacaoUtil;

public class UsuariosImpl implements UsuariosQueries {
	
	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@Transactional(readOnly = true)
	public Optional<Usuario> porEmailEAtivo(String email) {
		String jpql = "from Usuario u where lower(u.email) = lower(:email) and u.ativo = true";
		return manager.createQuery(jpql, Usuario.class).setParameter("email", email).getResultList().stream().findFirst();
	}

	@Override
	public List<String> permissoes(Usuario usuario) {
		String jpql = "select distinct p.nome from Usuario u inner join u.grupos g inner join g.permissoes p where u = :usuario";
		return manager.createQuery(jpql, String.class).setParameter("usuario", usuario).getResultList();
	}

//	@SuppressWarnings("unchecked")
//	@Override
//	@Transactional(readOnly = true)
//	public List<Usuario> filtrar(UsuarioFilter filtro) {
//		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
//		adicionarFiltro(filtro, criteria);
//		
//		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
//		return criteria.list();
//		
//	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<Usuario> filtrar(UsuarioFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);

		List<Usuario> filtrados = criteria.list();
		filtrados.forEach(u -> Hibernate.initialize(u.getGrupos())); //para inicializar os grupos e evitar o lazy load
		
		return new PageImpl<>(filtrados, pageable, total(filtro));
	}
	
	private Long total(UsuarioFilter filtro){
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
		criteria.setProjection(Projections.rowCount());
		adicionarFiltro(filtro, criteria);
		return (Long)criteria.uniqueResult();
	}
	
	private void adicionarFiltro(UsuarioFilter filtro, Criteria criteria) {
		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}
			
			if (!StringUtils.isEmpty(filtro.getEmail())) {
				criteria.add(Restrictions.ilike("email", filtro.getEmail(), MatchMode.START));
			}
			
			//para evitar problemas do lazy load
//			criteria.createAlias("grupos", "g", JoinType.LEFT_OUTER_JOIN);
			
			if (filtro.getGrupos() != null && !filtro.getGrupos().isEmpty()) {
				List<Criterion> subqueries = new ArrayList<>();
				for (Long codigoGrupo : filtro.getGrupos().stream().mapToLong(Grupo::getCodigo).toArray()) {
					DetachedCriteria dc = DetachedCriteria.forClass(UsuarioGrupo.class);
					dc.add(Restrictions.eq("id.grupo.codigo", codigoGrupo));
					dc.setProjection(Projections.property("id.usuario"));
					
					subqueries.add(Subqueries.propertyIn("codigo", dc));
				}
				
				Criterion[] criterions = new Criterion[subqueries.size()];
				criteria.add(Restrictions.and(subqueries.toArray(criterions)));
			}
		}
		
	}

	@Transactional(readOnly = true)
	public Usuario buscarComGrupos(Long codigo) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
		criteria.createAlias("grupos", "g", JoinType.LEFT_OUTER_JOIN)
			.add(Restrictions.eq("codigo", codigo))
			.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return (Usuario) criteria.uniqueResult();
	}
}
