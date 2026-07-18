package com.epiis.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epiis.finalproject.entity.EntityRole;
import java.util.Optional;


public interface RepositoryRole extends JpaRepository<EntityRole, String>{
	Optional<EntityRole> findByNameRole(String nameRole);
}
