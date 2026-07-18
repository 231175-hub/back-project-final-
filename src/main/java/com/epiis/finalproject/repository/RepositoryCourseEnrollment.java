package com.epiis.finalproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epiis.finalproject.entity.EntityCourseEnrollment;

public interface RepositoryCourseEnrollment extends JpaRepository<EntityCourseEnrollment, String>{
	
	@Query("SELECT ce FROM EntityCourseEnrollment ce WHERE ce.parentCourse.idCourse = :idCourse AND ce.parentPeriod.idPeriod = :idPeriod AND ce.isAssigned = false")
    List<EntityCourseEnrollment> findUnassignedStudents(@Param("idCourse") String idCourse, @Param("idPeriod") String idPeriod);
	
	@Query("SELECT ce FROM EntityCourseEnrollment ce WHERE ce.parentStudent.idStudent = :idStudent AND ce.parentCourse.idCourse = :idCourse AND ce.parentPeriod.idPeriod = :idPeriod")
    Optional<EntityCourseEnrollment> findByStudentCourseAndPeriod(@Param("idStudent") String idStudent, @Param("idCourse") String idCourse, @Param("idPeriod") String idPeriod);
	
    @Query("SELECT ce FROM EntityCourseEnrollment ce " + "JOIN FETCH ce.parentCourse c " + "JOIN FETCH ce.parentPeriod p " + "WHERE ce.parentStudent.idStudent = :idStudent " + "ORDER BY p.yearPeriod ASC, p.numberPeriod ASC")
    List<EntityCourseEnrollment> findHistorialByStudent(@Param("idStudent") String idStudent);

    @Query("SELECT COALESCE(SUM(ce.parentCourse.credits), 0) FROM EntityCourseEnrollment ce " +
           "WHERE ce.parentStudent.idStudent = :idStudent AND ce.status = 'APROBADO'")
    int sumApprovedCreditsByStudent(@Param("idStudent") String idStudent);

    @Query("SELECT COALESCE(SUM(ce.finalScore * ce.parentCourse.credits) * 1.0 / NULLIF(SUM(ce.parentCourse.credits), 0), 0.0) " +
           "FROM EntityCourseEnrollment ce " +
           "WHERE ce.parentStudent.idStudent = :idStudent AND ce.finalScore IS NOT NULL")
    double calculateWeightedAverageByStudent(@Param("idStudent") String idStudent);
}
