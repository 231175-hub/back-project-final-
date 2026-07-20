package com.epiis.finalproject.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epiis.finalproject.entity.EntityUser;

import java.util.List;
import java.util.Optional;

public interface RepositoryUser extends JpaRepository<EntityUser, String>{
	Optional<EntityUser> findByEmail(String email);
	Optional<EntityUser> findFirstByEmail(String email);
	
	@Query("SELECT u FROM EntityUser u WHERE u.parentRole.nameRole = 'Profesor' AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.surName) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<EntityUser> searchProfessorsByTerm(@Param("query") String query, PageRequest pageRequest);
}
