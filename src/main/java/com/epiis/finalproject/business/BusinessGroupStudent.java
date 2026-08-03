package com.epiis.finalproject.business;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.epiis.finalproject.helper.DateUtil;
import com.epiis.finalproject.helper.ResponseMapBuilder;

import org.springframework.stereotype.Service;

import com.epiis.finalproject.dto.request.groupstudent.RequestGroupStudentUpdate;
import com.epiis.finalproject.dto.request.groupstudent.RequestGroupStundentInsert;
import com.epiis.finalproject.dto.response.group.ResponseGroupDeleteById;
import com.epiis.finalproject.dto.response.groupstudent.ResponseGroupStudentGetAll;
import com.epiis.finalproject.dto.response.groupstudent.ResponseGroupStudentGetById;
import com.epiis.finalproject.dto.response.groupstudent.ResponseGroupStudentInsert;
import com.epiis.finalproject.dto.response.groupstudent.ResponseGroupStudentUpdate;
import com.epiis.finalproject.entity.EntityGroup;
import com.epiis.finalproject.entity.EntityGroupStudent;
import com.epiis.finalproject.entity.EntityStudent;
import com.epiis.finalproject.repository.RepositoryGroup;
import com.epiis.finalproject.repository.RepositoryGroupStudent;

@Service
public class BusinessGroupStudent {
	private final RepositoryGroupStudent repositoryGroupStudent;
	
	private final RepositoryGroup repositoryGroup;
	
	public BusinessGroupStudent(RepositoryGroupStudent repositoryGroupStudent, RepositoryGroup repositoryGroup) {
		this.repositoryGroupStudent = repositoryGroupStudent;
		this.repositoryGroup = repositoryGroup;
	}
	
	public ResponseGroupStudentInsert insert(RequestGroupStundentInsert request) {
		ResponseGroupStudentInsert response = new ResponseGroupStudentInsert();
		
		EntityGroupStudent entityGroupStudent = new EntityGroupStudent();
		EntityStudent entityStudent = new EntityStudent();
		
		EntityGroup entityGroup = repositoryGroup.findGroupWithLessStudents(request.getIdCourse());
		entityStudent.setIdStudent(request.getIdStudent());
		
		entityGroupStudent.setIdGroupStudent(UUID.randomUUID().toString());
		entityGroupStudent.setParentGroup(entityGroup);
		entityGroupStudent.setParentStudent(entityStudent);
		entityGroupStudent.setCreatedAt(DateUtil.currentSqlDate());
		entityGroupStudent.setUpdatedAt(entityGroupStudent.getCreatedAt());
		
		repositoryGroupStudent.save(entityGroupStudent);
		
		response.success();
		response.getListMessage().add("Estudiante AsignadoCorrectamente");
		
		return response;
	}
	
	public Map<String, Object> getAll() {
		return ResponseMapBuilder.buildDataMap(
				new ResponseGroupStudentGetAll(),
				"Estudiantes/Grupos Entregados Correctamente",
				repositoryGroupStudent.findAll());
	}
	
	public Map<String, Object> getById(String idGroupStudent) {
		return ResponseMapBuilder.buildDataMap(
				new ResponseGroupStudentGetById(),
				"Estudiante/Grupo Encontrado Correctamente",
				repositoryGroupStudent.findById(idGroupStudent));
	}
	
	public ResponseGroupDeleteById deleteById(String idGroupStudent) {
		ResponseGroupDeleteById response = new ResponseGroupDeleteById();
		
		repositoryGroupStudent.deleteById(idGroupStudent);
		
		response.success();
		response.getListMessage().add("Estudiante/Grupo Eliminado Correctamente");
		
		return response;
	}
	
	public ResponseGroupStudentUpdate update(String idGroupStudent, RequestGroupStudentUpdate request) {
		ResponseGroupStudentUpdate response = new ResponseGroupStudentUpdate();
		
		Optional<EntityGroupStudent> optional = repositoryGroupStudent.findById(idGroupStudent);
		
		if (optional.isPresent()) {
			EntityGroupStudent entityGroupStudent = optional.get();
			EntityGroup entityGroup = repositoryGroup.findGroupWithLessStudents(request.getIdCourse());
			EntityStudent entityStudent = new EntityStudent();
			
			entityStudent.setIdStudent(request.getIdStudent());
			
			entityGroupStudent.setParentGroup(entityGroup);
			entityGroupStudent.setParentStudent(entityStudent);
			entityGroupStudent.setUpdatedAt(DateUtil.currentSqlDate());
			
			repositoryGroupStudent.save(entityGroupStudent);
			
			response.success();
			response.getListMessage().add("Estudiante/Grupo Actualizado Correctamente");
			
			return response;
		}
		
		response.error();
		response.getListMessage().add("Error Estudiante/Group no se Actualizo");
		
		return response;
	}
}
