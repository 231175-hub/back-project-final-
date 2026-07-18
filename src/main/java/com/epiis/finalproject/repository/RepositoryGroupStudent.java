package com.epiis.finalproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epiis.finalproject.dto.response.schedule.ResponseScheduleGetAll;
import com.epiis.finalproject.entity.EntityGroup;
import com.epiis.finalproject.entity.EntityGroupStudent;

public interface RepositoryGroupStudent extends JpaRepository<EntityGroupStudent,String>{
	
	@Query("SELECT gs.parentGroup FROM EntityGroupStudent gs WHERE gs.parentStudent.idStudent = :idStudent")
    List<EntityGroup> findGroupsByStudentId(@Param("idStudent") String idStudent);
	
	@Query("""
	        SELECT new com.epiis.finalproject.dto.response.schedule.ResponseScheduleGetAll(
	            s.dayWeek, 
	            CAST(s.startTime AS string), 
	            CAST(s.endTime AS string), 
	            s.classroom, 
	            c.nameCourse,
	            g.nameGroup
	        ) 
	        FROM EntityGroupStudent gs
	        JOIN gs.parentGroup g
	        JOIN g.parentCourse c
	        JOIN g.childSchedule s
	        WHERE gs.parentStudent.idStudent = :idKeycloak
	    """)
	List<ResponseScheduleGetAll> findCustomScheduleByStudentId(@Param("idKeycloak") String idKeycloak);
	
	@Query("SELECT COALESCE(SUM(c.credits), 0) FROM EntityGroupStudent gs " +
	           "JOIN gs.parentGroup g " +
	           "JOIN g.parentCourse c " +
	           "JOIN g.parentAcademicperiod p " +
	           "WHERE gs.parentStudent.idStudent = :idStudent AND p.status = 'Activo'")
	Integer sumTotalCreditsByStudentAndActivePeriod(@Param("idStudent") String idStudent);
	
	List<EntityGroupStudent> findByParentGroup(EntityGroup parentGroup);
	
	java.util.Optional<EntityGroupStudent> findByParentGroupAndParentStudent(EntityGroup parentGroup, com.epiis.finalproject.entity.EntityStudent parentStudent);

	@Query("SELECT DISTINCT gs FROM EntityGroupStudent gs " +
	       "LEFT JOIN FETCH gs.parentGroup g " +
	       "LEFT JOIN FETCH g.parentCourse c " +
	       "LEFT JOIN FETCH gs.childUnitscore us " +
	       "WHERE gs.parentStudent.idStudent = :idStudent")
	List<EntityGroupStudent> findEnrollmentsWithGroupAndScoresByStudentId(@Param("idStudent") String idStudent);
}
