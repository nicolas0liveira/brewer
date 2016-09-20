package com.aftermidnight.brewer.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aftermidnight.brewer.model.Venda;
import com.aftermidnight.brewer.repository.helper.venda.VendasQueries;


public interface Vendas extends Serializable, JpaRepository<Venda, Long>, VendasQueries {

	


}
