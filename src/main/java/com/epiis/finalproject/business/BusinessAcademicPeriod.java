package com.epiis.finalproject.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.epiis.finalproject.dto.request.academicperiod.RequestAcademicPeriodInsert;
import com.epiis.finalproject.dto.request.academicperiod.RequestAcademicPeriodUpdate;
import com.epiis.finalproject.dto.response.academicperiod.ResponseAcademicPeriodDeleteById;
import com.epiis.finalproject.dto.response.academicperiod.ResponseAcademicPeriodGetAll;
import com.epiis.finalproject.dto.response.academicperiod.ResponseAcademicPeriodInsert;
import com.epiis.finalproject.dto.response.academicperiod.ResponseAcademicPeriodUpdate;
import com.epiis.finalproject.entity.EntityAcademicPeriod;
import com.epiis.finalproject.repository.RepositoryAcademicperiod;
import com.epiis.finalproject.staticdata.EnumAcademicPeriod;

@Service
public class BusinessAcademicPeriod {
	private final RepositoryAcademicperiod repositoryAcademicperiod;
	
	public BusinessAcademicPeriod(RepositoryAcademicperiod repositoryAcademicperiod) {
		this.repositoryAcademicperiod = repositoryAcademicperiod;
	}
	
	public ResponseAcademicPeriodInsert insert(RequestAcademicPeriodInsert request) {
		ResponseAcademicPeriodInsert response = new ResponseAcademicPeriodInsert();
		
		EntityAcademicPeriod entityAcademicPeriod = new EntityAcademicPeriod();
		
		entityAcademicPeriod.setIdPeriod(UUID.randomUUID().toString());
		entityAcademicPeriod.setYearPeriod(request.getYearPeriod());
		entityAcademicPeriod.setNumberPeriod(request.getNumberPeriod());
		entityAcademicPeriod.setStartDate(request.getStartDate());
		entityAcademicPeriod.setEndDate(request.getEndDate());
		entityAcademicPeriod.setStatus(request.getStatus() != null ? request.getStatus() : EnumAcademicPeriod.ACTIVE.toString());
		entityAcademicPeriod.setCreatedAt(new java.sql.Date(new Date().getTime()));
		entityAcademicPeriod.setUpdatedAt(entityAcademicPeriod.getCreatedAt());
		
		if ("Activo".equals(request.getStatus())) {
			Optional<EntityAcademicPeriod> activePeriod = repositoryAcademicperiod.findByStatus("Activo");
			if (activePeriod.isPresent()) {
				response.error();
				response.getListMessage().add("Ya se encuentra registrado un semestre activo.");
				return response;
			}
		}

		repositoryAcademicperiod.save(entityAcademicPeriod);
		
		response.success();
		response.getListMessage().add("Periodo academico registrado exitosamente");
		
		return  response;
	}
	
	public Map<String, Object> getAll() {
		ResponseAcademicPeriodGetAll response = new ResponseAcademicPeriodGetAll();
		
		Map<String, Object> res = new HashMap<>();
		
		List<EntityAcademicPeriod> entityAcademicPeriod = repositoryAcademicperiod.findAll();
		
		response.success();
		response.getListMessage().add("Periodos academicos Entregados Correctamente");
		
		res.put("message", response);
		res.put("data", entityAcademicPeriod);
		
		return res;
	}
	
	public Map<String, Object> getById(String idPeriod) {
		ResponseAcademicPeriodGetAll response = new ResponseAcademicPeriodGetAll();
		
		Map<String, Object> res = new HashMap<>();
		
		Optional<EntityAcademicPeriod> entityAcademicPeriod = repositoryAcademicperiod.findById(idPeriod);
		
		response.success();
		response.getListMessage().add("Periodo academico encontrado correctamente");
		
		res.put("message", response);
		res.put("data", entityAcademicPeriod);
		
		return res;
	}
	
	public ResponseAcademicPeriodDeleteById deleteById(String idPeriod) {
		ResponseAcademicPeriodDeleteById response = new ResponseAcademicPeriodDeleteById();
		
		repositoryAcademicperiod.deleteById(idPeriod);
		
		response.success();
		response.getListMessage().add("Periodo academico eliminado exitosamente");
		
		return response;
	}
	
	public ResponseAcademicPeriodUpdate update(String idPeriod, RequestAcademicPeriodUpdate request) {
		ResponseAcademicPeriodUpdate response = new ResponseAcademicPeriodUpdate();
		
		Optional<EntityAcademicPeriod> optional = repositoryAcademicperiod.findById(idPeriod);
		
		if (optional.isPresent()) {
			EntityAcademicPeriod entityAcademicPeriod = optional.get();
			
			if ("Activo".equals(request.getStatus())) {
				Optional<EntityAcademicPeriod> activePeriod = repositoryAcademicperiod.findByStatus("Activo");
				if (activePeriod.isPresent() && !activePeriod.get().getIdPeriod().equals(idPeriod)) {
					response.error();
					response.getListMessage().add("Ya se encuentra registrado un semestre activo.");
					return response;
				}
			}

			entityAcademicPeriod.setYearPeriod(request.getYearPeriod());
			entityAcademicPeriod.setNumberPeriod(request.getNumberPeriod());
			entityAcademicPeriod.setStartDate(request.getStartDate());
			entityAcademicPeriod.setEndDate(request.getEndDate());
			entityAcademicPeriod.setStatus(request.getStatus());
			entityAcademicPeriod.setUpdatedAt(new java.sql.Date(new Date().getTime()));
			
			repositoryAcademicperiod.save(entityAcademicPeriod);
			
			response.success();
			response.getListMessage().add("Periodo academico actualizado correctamente");
			
			return response;
		}
		
		response.error();
		response.getListMessage().add("Error Periodo academico no actualizado");
		
		return response;
	}
}
