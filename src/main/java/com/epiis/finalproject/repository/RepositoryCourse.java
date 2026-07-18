package com.epiis.finalproject.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epiis.finalproject.entity.EntityCourse;

public interface RepositoryCourse extends JpaRepository<EntityCourse, String>{
	@Query("SELECT c FROM EntityCourse c WHERE c.parentSchool.idSchool = :idSchool AND (LOWER(c.nameCourse) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.code) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<EntityCourse> searchCoursesByTermAndSchool(@Param("query") String query, @Param("idSchool") String idSchool, PageRequest pageRequest);
	
	EntityCourse findByNameCourse(String nameCourse);
}
