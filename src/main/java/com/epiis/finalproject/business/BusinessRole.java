package com.epiis.finalproject.business;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.epiis.finalproject.dto.request.role.RequestRoleInsert;
import com.epiis.finalproject.dto.request.role.RequestRoleUpdate;
import com.epiis.finalproject.dto.response.role.ResponseRoleDeleteById;
import com.epiis.finalproject.dto.response.role.ResponseRoleGetById;
import com.epiis.finalproject.dto.response.role.ResponseRoleInsert;
import com.epiis.finalproject.dto.response.role.ResponseRoleUpdate;
import com.epiis.finalproject.entity.EntityRole;
import com.epiis.finalproject.helper.DateUtil;
import com.epiis.finalproject.helper.ResponseMapBuilder;
import com.epiis.finalproject.repository.RepositoryRole;

@Service
public class BusinessRole {
	private final RepositoryRole repositoryRole;
	
	public BusinessRole(RepositoryRole repositoryRole) {
		this.repositoryRole = repositoryRole;
	}
	
	public ResponseRoleInsert insert(RequestRoleInsert request) {
		ResponseRoleInsert response = new ResponseRoleInsert();
		
		EntityRole entityRole = new EntityRole();
		entityRole.setIdRole(UUID.randomUUID().toString());
		entityRole.setNameRole(request.getNameRole());
		entityRole.setCreatedAt(DateUtil.currentSqlDate());
		entityRole.setUpdatedAt(entityRole.getCreatedAt());
		
		repositoryRole.save(entityRole);
		
		response.success();
		response.getListMessage().add("Rol registrado exitosamente");
		
		return response;
	}
	
	public Map<String, Object> getAll() {
		return ResponseMapBuilder.buildDataMap(
				new ResponseRoleInsert(),
				"Roles entregados correctamente",
				repositoryRole.findAll());
	}
	
	public Map<String, Object> getById(String idRole) {
		Optional<EntityRole> entityRole = repositoryRole.findById(idRole);
		return ResponseMapBuilder.buildDataMap(
				new ResponseRoleGetById(),
				"Rol encontrado correctamente",
				entityRole);
	}
	
	public ResponseRoleDeleteById deleteById(String idRole) {
		ResponseRoleDeleteById response = new ResponseRoleDeleteById();
		repositoryRole.deleteById(idRole);
		response.success();
		response.getListMessage().add("Rol eliminado correctamente");
		return response;
	}
	
	public ResponseRoleUpdate update(String idRole, RequestRoleUpdate request) {
		ResponseRoleUpdate response = new ResponseRoleUpdate();
		
		Optional<EntityRole> optional = repositoryRole.findById(idRole);
		
		if (optional.isPresent()) {
			EntityRole entityRole = optional.get();
			entityRole.setNameRole(request.getNameRole());
			entityRole.setUpdatedAt(DateUtil.currentSqlDate());
			repositoryRole.save(entityRole);
			response.success();
			response.getListMessage().add("Rol actualizado correctamente");
			return response;
		}
		
		response.error();
		response.getListMessage().add("Error el rol no se actualizo");
		return response;
	}
}
