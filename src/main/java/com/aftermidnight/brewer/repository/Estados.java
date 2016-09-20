package com.aftermidnight.brewer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aftermidnight.brewer.model.Estado;

public interface Estados extends JpaRepository<Estado, Long> {
	
}
