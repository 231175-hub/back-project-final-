package com.epiis.finalproject.business;

import com.epiis.finalproject.repository.RepositoryGroupStudent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epiis.finalproject.dto.request.schedule.RequestScheduleInsert;
import com.epiis.finalproject.dto.request.schedule.RequestScheduleUpdate;
import com.epiis.finalproject.dto.response.schedule.ResponseScheduleDeleteById;
import com.epiis.finalproject.dto.response.schedule.ResponseScheduleGetAll;
import com.epiis.finalproject.dto.response.schedule.ResponseScheduleGetById;
import com.epiis.finalproject.dto.response.schedule.ResponseScheduleInsert;
import com.epiis.finalproject.dto.response.schedule.ResponseScheduleUpdate;
import com.epiis.finalproject.entity.EntityGroup;
import com.epiis.finalproject.entity.EntityProfessor;
import com.epiis.finalproject.entity.EntitySchedule;
import com.epiis.finalproject.entity.EntityUser;
import com.epiis.finalproject.repository.RepositoryCourse;
import com.epiis.finalproject.repository.RepositoryGroup;
import com.epiis.finalproject.repository.RepositorySchedule;
import com.epiis.finalproject.repository.RepositoryUser;

@Service
public class BusinessSchedule {
	private final RepositoryGroupStudent repositoryGroupStudent;
	private final RepositorySchedule repositorySchedule;
	private final RepositoryGroup repositoryGroup;
	private final RepositoryUser repositoryUser;
	
	public BusinessSchedule(RepositorySchedule repositorySchedule, RepositoryCourse repositoryCourse, RepositoryGroupStudent repositoryGroupStudent, RepositoryUser repositoryUser, RepositoryGroup repositoryGroup) {
		this.repositorySchedule = repositorySchedule;
		this.repositoryGroupStudent = repositoryGroupStudent;
		this.repositoryGroup = repositoryGroup;
		this.repositoryUser = repositoryUser;
	}
	
	@Transactional
	public ResponseScheduleInsert insert(List<RequestScheduleInsert> listRequest) {
		ResponseScheduleInsert response = new ResponseScheduleInsert();
		
		List<EntitySchedule> listSchedules = new ArrayList<>();
		
		for(RequestScheduleInsert schedule : listRequest) {
			
			EntityGroup entityGroup = repositoryGroup.findById(schedule.getIdGroup())
					.orElseThrow(() -> new RuntimeException("Grupo no encontrado en la base de datos"));
			
			if (schedule.getIdProfessor() != null && !schedule.getIdProfessor().isEmpty()) {
				EntityUser usuarioProfesor = repositoryUser.findById(schedule.getIdProfessor())
						.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
				
				if (usuarioProfesor.getChildProfessor() == null) {
					throw new RuntimeException("El usuario seleccionado no está registrado como profesor.");
				}
				EntityProfessor profesorReal = usuarioProfesor.getChildProfessor();
				
				entityGroup.setParentProfessor(profesorReal); 
				
				repositoryGroup.save(entityGroup);
			}
			
			if (schedule.getSchedules() != null) {
				for(EntitySchedule entitySchedule : schedule.getSchedules()) {
					entitySchedule.setParentGroup(entityGroup);
					
					entitySchedule.setIdSchedule(UUID.randomUUID().toString());
					entitySchedule.setCreatedAt(new java.sql.Date(new Date().getTime()));
					entitySchedule.setUpdatedAt(entitySchedule.getCreatedAt());
					
					listSchedules.add(entitySchedule);
				}
			}
		}
		
		repositorySchedule.saveAll(listSchedules);
		
		response.success();
		response.getListMessage().add("Horario y docentes registrados correctamente");
		
		return response;
	}
	
	public Map<String, Object> getById(String idSchedule){
		ResponseScheduleGetById response = new ResponseScheduleGetById();
		
		Map<String, Object> res = new HashMap<>();
		
		Optional<EntitySchedule> entitySchedule = repositorySchedule.findById(idSchedule);
		
		response.success();
		response.getListMessage().add("Horario Encontrado exitosamente");
		
		res.put("message", response);
		res.put("data", entitySchedule);
		
		return res;
	}
	
	public ResponseScheduleDeleteById deleteById(String idSchedule) {
		ResponseScheduleDeleteById response = new ResponseScheduleDeleteById();
		
		repositorySchedule.deleteById(idSchedule);
		
		response.success();
		response.getListMessage().add("Horario Eliminado exitosamente");
		
		return response;
	}
	
	public ResponseScheduleUpdate update(String idSchedule, RequestScheduleUpdate request) {
		ResponseScheduleUpdate response = new ResponseScheduleUpdate();
		
		Optional<EntitySchedule> optional = repositorySchedule.findById(idSchedule);
		
		if (optional.isPresent()) {
			EntitySchedule entitySchedule = optional.get();
			
			EntityGroup entityGroup = new EntityGroup();
			
			entityGroup.setIdGroup(request.getIdGroup());
			
			entitySchedule.setDayWeek(request.getDayWeek());
			entitySchedule.setStartTime(request.getStartTime());
			entitySchedule.setEndTime(request.getEndTime());
			entitySchedule.setClassroom(request.getClassroom());
			entitySchedule.setParentGroup(entityGroup);
			entitySchedule.setUpdatedAt(new java.sql.Date(new Date().getTime()));
			
			repositorySchedule.save(entitySchedule);
			
			response.success();
			response.getListMessage().add("Horario Actualizado Correctamente");
			
			return response;
		}
		
		response.error();
		response.getListMessage().add("Error el Horario no se Actualizo");
		
		return response;
	}
	
	
	public List<ResponseScheduleGetAll> getStudentSchedule(String idStudentKeycloak) {
	    return repositoryGroupStudent.findCustomScheduleByStudentId(idStudentKeycloak);
	}
}
