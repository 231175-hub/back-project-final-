package com.epiis.finalproject.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
		
		Optional<EntityAcademicPeriod> optionalAcademicPeriod = repositoryAcademicperiod.findByStatus(EnumAcademicPeriod.ACTIVE.toString());
		if (optionalAcademicPeriod.isEmpty()) {
			response.error();
			response.getListMessage().add("Error: No hay un periodo académico activo actualmente en el sistema.");
			return response;
		}
		EntityAcademicPeriod entityAcademicPeriod = optionalAcademicPeriod.get();
		
		Optional<EntityStudent> optionalStudent = repositoryStudent.findById(request.getIdStudent());
		if (optionalStudent.isEmpty()) {
			response.error();
			response.getListMessage().add("Error: El estudiante especificado no existe en el sistema.");
			return response;
		}
		EntityStudent entityStudent = optionalStudent.get();
		
		for(String idCourse : request.getCourses()) {
			Optional<EntityCourse> optionalCourse = repositoryCourse.findById(idCourse);
			if (optionalCourse.isEmpty()) {
				response.error();
				response.getListMessage().add("Error: Uno de los cursos seleccionados no existe en el sistema.");
				return response;
			}
			EntityCourse entityCourse = optionalCourse.get();

			EntityCourseEnrollment entityCourseEnrollment = new EntityCourseEnrollment();
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
