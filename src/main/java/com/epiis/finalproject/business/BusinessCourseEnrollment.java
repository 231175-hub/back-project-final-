package com.epiis.finalproject.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.epiis.finalproject.dto.request.courseenrollment.RequestCourseEnrollmentInsert;
import com.epiis.finalproject.dto.response.courseenrollment.ResponseCourseEnrollmentInsert;
import com.epiis.finalproject.entity.EntityAcademicPeriod;
import com.epiis.finalproject.entity.EntityCourse;
import com.epiis.finalproject.entity.EntityCourseEnrollment;
import com.epiis.finalproject.entity.EntityStudent;
import com.epiis.finalproject.repository.RepositoryAcademicperiod;
import com.epiis.finalproject.repository.RepositoryCourse;
import com.epiis.finalproject.repository.RepositoryCourseEnrollment;
import com.epiis.finalproject.repository.RepositoryStudent;
import com.epiis.finalproject.staticdata.EnumAcademicPeriod;

@Service
public class BusinessCourseEnrollment {
	private final RepositoryCourseEnrollment repositoryCourseEnrollment; 
	private final RepositoryAcademicperiod repositoryAcademicperiod;
	private final RepositoryStudent repositoryStudent;
	private final RepositoryCourse repositoryCourse;
	
	public BusinessCourseEnrollment(RepositoryCourseEnrollment repositoryCourseEnrollment, RepositoryAcademicperiod repositoryAcademicperiod, RepositoryStudent repositoryStudent, RepositoryCourse repositoryCourse) {
		this.repositoryCourseEnrollment = repositoryCourseEnrollment;
		this.repositoryAcademicperiod = repositoryAcademicperiod;
		this.repositoryCourse = repositoryCourse;
		this.repositoryStudent = repositoryStudent;
	}
	
	public ResponseCourseEnrollmentInsert insert(RequestCourseEnrollmentInsert request) {
		ResponseCourseEnrollmentInsert response = new ResponseCourseEnrollmentInsert();
				
		List<EntityCourseEnrollment> listCourseEnrollments = new ArrayList<>();
		
		EntityAcademicPeriod entityAcademicPeriod  = repositoryAcademicperiod.findByStatus(EnumAcademicPeriod.ACTIVE.toString()).orElseThrow(() -> new RuntimeException("Error: No hay un periodo académico activo actualmente"));
		
		EntityStudent entityStudent = repositoryStudent.getReferenceById(request.getIdStudent());
		
		for(String idCourse : request.getCourses()) {
			EntityCourseEnrollment entityCourseEnrollment = new EntityCourseEnrollment();
			
			EntityCourse entityCourse = repositoryCourse.getReferenceById(idCourse);
			
			entityCourseEnrollment.setIdEnrollment(UUID.randomUUID().toString());
			entityCourseEnrollment.setParentStudent(entityStudent);
			entityCourseEnrollment.setParentCourse(entityCourse);
			entityCourseEnrollment.setAssigned(false);
			entityCourseEnrollment.setParentPeriod(entityAcademicPeriod);
			entityCourseEnrollment.setCreatedAt(new java.sql.Date(new Date().getTime()));
			entityCourseEnrollment.setUpdatedAt(entityCourseEnrollment.getCreatedAt());
			
			listCourseEnrollments.add(entityCourseEnrollment);
		}
		
		repositoryCourseEnrollment.saveAll(listCourseEnrollments);
		
		response.success();
		response.getListMessage().add("El alumno se matriculo correctamente");
		
		return response;
	}
}
