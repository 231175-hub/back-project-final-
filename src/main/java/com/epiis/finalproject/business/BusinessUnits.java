package com.epiis.finalproject.business;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.epiis.finalproject.dto.request.units.RequestUnitsInsert;
import com.epiis.finalproject.dto.request.units.RequestUnitsUpdate;
import com.epiis.finalproject.dto.response.units.ResponseUnitsDeleteById;
import com.epiis.finalproject.dto.response.units.ResponseUnitsGetAll;
import com.epiis.finalproject.dto.response.units.ResponseUnitsGetById;
import com.epiis.finalproject.dto.response.units.ResponseUnitsInsert;
import com.epiis.finalproject.dto.response.units.ResponseUnitsUpdate;
import com.epiis.finalproject.entity.EntityGroup;
import com.epiis.finalproject.entity.EntityUnits;
import com.epiis.finalproject.helper.DateUtil;
import com.epiis.finalproject.helper.ResponseMapBuilder;
import com.epiis.finalproject.repository.RepositoryUnits;

@Service
public class BusinessUnits {
	private final RepositoryUnits repositoryUnits;
	
	public BusinessUnits(RepositoryUnits repositoryUnits) {
		this.repositoryUnits = repositoryUnits; 
	}
	
	public ResponseUnitsInsert insert(RequestUnitsInsert request) {
		ResponseUnitsInsert response = new ResponseUnitsInsert();
		
		EntityGroup entityGroup = new EntityGroup();
		entityGroup.setIdGroup(request.getIdGroup());
		
		EntityUnits entityUnits = new EntityUnits();
		entityUnits.setIdUnits(UUID.randomUUID().toString());
		entityUnits.setNumberUnit(request.getNumberUnit());
		entityUnits.setNameUnit(request.getNameUnit());
		entityUnits.setParentGroup(entityGroup);
		entityUnits.setCreatedAt(DateUtil.currentSqlDate());
		entityUnits.setUpdatedAt(entityUnits.getCreatedAt());
		
		repositoryUnits.save(entityUnits);
		
		response.success();
		response.getListMessage().add("Unidad Registrada Correctamente");
		return response;
	}
	
	public Map<String, Object> getAll() {
		return ResponseMapBuilder.buildDataMap(
				new ResponseUnitsGetAll(),
				"Unidades Entregadas Correctamente",
				repositoryUnits.findAll());
	}
	
	public Map<String, Object> getById(String idUnits) {
		return ResponseMapBuilder.buildDataMap(
				new ResponseUnitsGetById(),
				"Unidad encontrada exitosamente",
				repositoryUnits.findById(idUnits));
	}
	
	public ResponseUnitsDeleteById deleteById(String idUnits) {
		ResponseUnitsDeleteById response = new ResponseUnitsDeleteById();
		repositoryUnits.deleteById(idUnits);
		response.success();
		response.getListMessage().add("Unidad Eliminada Exitosamente");
		return response;
	}
	
	public ResponseUnitsUpdate update(String idUnits, RequestUnitsUpdate request) {
		ResponseUnitsUpdate response = new ResponseUnitsUpdate();
		
		Optional<EntityUnits> optional = repositoryUnits.findById(idUnits);
		
		if (optional.isPresent()) {
			EntityUnits entityUnits = optional.get();
			EntityGroup entityGroup = new EntityGroup();
			entityGroup.setIdGroup(request.getIdGroup());
			entityUnits.setNumberUnit(request.getNumberUnit());
			entityUnits.setNameUnit(request.getNameUnit());
			entityUnits.setParentGroup(entityGroup);
			repositoryUnits.save(entityUnits);
			response.success();
			response.getListMessage().add("Unidad Actualizada correctamente");
			return response;
		}
		
		response.error();
		response.getListMessage().add("Error la Unidad no se Actualizo");
		return response;
	}
}
