package com.aftermidnight.brewer.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aftermidnight.brewer.model.Cidade;
import com.aftermidnight.brewer.repository.helper.cidade.CidadesQueries;

@Repository
public interface Cidades extends Serializable, JpaRepository<Cidade, Long>, CidadesQueries {

	public List<Cidade> findByEstadoCodigo(Long codigoEstado);

}
