package com.epiis.finalproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epiis.finalproject.entity.EntityStudent;

public interface RepositoryStudent extends JpaRepository<EntityStudent, String>{
	@Query("SELECT s FROM EntityStudent s JOIN FETCH s.parentSchool JOIN FETCH s.parentUser")
	List<EntityStudent> studentsWithSchoolAndUser();
	
	@Query("SELECT s FROM EntityStudent s JOIN FETCH s.parentSchool JOIN FETCH s.parentUser WHERE s.idStudent = :idStudent")
	Optional<EntityStudent> findByIdWithSchoolAndUser(@Param("idStudent") String idStudent);
	
	@Query("SELECT s FROM EntityStudent s JOIN s.parentUser u WHERE s.parentSchool.idSchool = :idSchool AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.surName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.code) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<EntityStudent> searchStudentsByTermAndSchool(@Param("query") String query, @Param("idSchool") String idSchool, PageRequest pageRequest);

	Optional<EntityStudent> findByCode(String code);
}
