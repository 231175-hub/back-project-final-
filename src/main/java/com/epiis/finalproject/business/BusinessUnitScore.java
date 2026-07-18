package com.epiis.finalproject.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.epiis.finalproject.dto.request.unitscore.RequestUnitScoreInsert;
import com.epiis.finalproject.dto.request.unitscore.RequestUnitScoreUpdate;
import com.epiis.finalproject.dto.response.unitscore.ResponseUnitScoreDeleteById;
import com.epiis.finalproject.dto.response.unitscore.ResponseUnitScoreGetAll;
import com.epiis.finalproject.dto.response.unitscore.ResponseUnitScoreGetById;
import com.epiis.finalproject.dto.response.unitscore.ResponseUnitScoreInsert;
import com.epiis.finalproject.dto.response.unitscore.ResponseUnitScoreUpdate;
import com.epiis.finalproject.entity.EntityGroupStudent;
import com.epiis.finalproject.entity.EntityUnits;
import com.epiis.finalproject.entity.EntityUnitscore;
import com.epiis.finalproject.repository.RepositoryUnitScore;

@Service
public class BusinessUnitScore {
	private final RepositoryUnitScore repositoryUnitScore;
	
	public BusinessUnitScore(RepositoryUnitScore repositoryUnitScore) {
		this.repositoryUnitScore = repositoryUnitScore;
	}
	
	public ResponseUnitScoreInsert insert(RequestUnitScoreInsert request) {
		ResponseUnitScoreInsert response = new ResponseUnitScoreInsert();
		
		EntityUnitscore entityUnitscore = new EntityUnitscore();
		
		EntityGroupStudent entityGroupStudent = new EntityGroupStudent();
		EntityUnits entityUnits = new EntityUnits();
		
		entityGroupStudent.setIdGroupStudent(request.getIdGroupStudent());
		entityUnits.setIdUnits(request.getIdUnits());
		
		entityUnitscore.setIdUnitScore(UUID.randomUUID().toString());
		entityUnitscore.setParentGroupStudent(entityGroupStudent);
		entityUnitscore.setParentUnits(entityUnits);
		entityUnitscore.setScore(request.getScore());
		entityUnitscore.setCreatedAt(new java.sql.Date(new Date().getTime()));
		entityUnitscore.setUpdatedAt(entityUnitscore.getCreatedAt());
		
		repositoryUnitScore.save(entityUnitscore);
		
		response.success();
		response.getListMessage().add("Notas Ingresadas Correctamente");
		
		return response;
	}
	
	public Map<String, Object> getAll() {
		ResponseUnitScoreGetAll response = new ResponseUnitScoreGetAll();
		
		Map<String, Object> res = new HashMap<>();
		
		List<EntityUnitscore> entityUnitScore = repositoryUnitScore.findAll();
		
		response.success();
		response.getListMessage().add("Notas Entregadas Corretamente");
		
		res.put("message", response);
		res.put("data", entityUnitScore);
		
		return res;
	}
	
	public Map<String, Object> getById(String idUnitScore) {
		ResponseUnitScoreGetById response = new ResponseUnitScoreGetById();
		
		Map<String, Object> res = new HashMap<>();
		
		Optional<EntityUnitscore> entityUnitScore  = repositoryUnitScore.findById(idUnitScore);
		
		response.success();
		response.getListMessage().add("Nota Encontrada Corretamente");
		
		res.put("message", response);
		res.put("data", entityUnitScore);
		
		return res;
	}
	
	public ResponseUnitScoreDeleteById deleteById(String idUnitScore) {
		ResponseUnitScoreDeleteById response = new ResponseUnitScoreDeleteById();
		
		repositoryUnitScore.deleteById(idUnitScore);
		
		response.success();
		response.getListMessage().add("Notas Eliminada Correctamente");
		
		return response;
	}
	
	public ResponseUnitScoreUpdate update(String idUnitScore, RequestUnitScoreUpdate request) {
		ResponseUnitScoreUpdate response = new ResponseUnitScoreUpdate();
		
		Optional<EntityUnitscore> optional = repositoryUnitScore.findById(idUnitScore);
		
		if (optional.isPresent()) {
			EntityUnitscore entityUnitscore = optional.get();
			
			EntityGroupStudent entityGroupStudent = new EntityGroupStudent();
			EntityUnits entityUnits = new EntityUnits();
			
			entityGroupStudent.setIdGroupStudent(request.getIdGroupStudent());
			entityUnits.setIdUnits(request.getIdUnits());
			
			entityUnitscore.setParentGroupStudent(entityGroupStudent);
			entityUnitscore.setParentUnits(entityUnits);
			entityUnitscore.setScore(request.getScore());
			entityUnitscore.setUpdatedAt(new java.sql.Date(new Date().getTime()));
			
			response.success();
			response.getListMessage().add("Nota actualizada correctamente");
			
			return response;
		}
		
		response.error();
		response.getListMessage().add("Error la Nota no se Actualizo");
		
		return response;
	}
}
