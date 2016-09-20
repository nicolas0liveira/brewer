package com.aftermidnight.brewer.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aftermidnight.brewer.model.Cerveja;
import com.aftermidnight.brewer.repository.helper.cerveja.CervejasQueries;

@Repository
public interface Cervejas extends Serializable, JpaRepository<Cerveja, Long>, CervejasQueries{

}
