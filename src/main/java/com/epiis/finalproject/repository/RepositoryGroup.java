package com.epiis.finalproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epiis.finalproject.dto.response.group.ProfessorGroupProjection;
import com.epiis.finalproject.entity.EntityGroup;

public interface RepositoryGroup extends JpaRepository<EntityGroup, String>{
	@Query(value = """
			SELECT g.*
			     FROM tgroup g
			     LEFT JOIN tgroup_student gs
			         ON g.id_group = gs.id_group
			     WHERE g.id_course = :idCourse
			     GROUP BY g.id_group
			     ORDER BY COUNT(gs.id_student) ASC
			     LIMIT 1
			     """, nativeQuery = true)

	EntityGroup findGroupWithLessStudents(
			@Param("idCourse") String idCourse
	);

	
	@Query(value = "SELECT * FROM tgroup WHERE id_course = :idCourse", nativeQuery = true)
    List<EntityGroup> findAllGroupsByCourse(@Param("idCourse") String idCourse);
	
	@Query(value = """
            SELECT g.id_group as idGroup, 
                   g.name_group as nameGroup, 
                   c.name_course as nameCourse, 
                   c.code as courseCode,
                   (SELECT COUNT(*) FROM tgroup_student gs WHERE gs.id_group = g.id_group) as studentCount
            FROM tgroup g
            INNER JOIN tcourse c ON g.id_course = c.id_course
            WHERE g.id_professor = :idProfessor
            ORDER BY c.name_course ASC, g.name_group ASC
            """, nativeQuery = true)
    List<ProfessorGroupProjection> findGroupsByProfessor(@Param("idProfessor") String idProfessor);
}
