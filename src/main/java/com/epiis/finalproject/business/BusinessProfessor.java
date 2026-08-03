package com.epiis.finalproject.business;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.epiis.finalproject.helper.DateUtil;
import com.epiis.finalproject.helper.ResponseMapBuilder;
import com.epiis.finalproject.helper.UserMutationHelper;
import java.util.HashMap;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.epiis.finalproject.dto.request.professor.RequestProfessorInsert;
import com.epiis.finalproject.dto.request.professor.RequestProfessorUpdate;
import com.epiis.finalproject.dto.request.user.RequestUserUpdatePassword;
import com.epiis.finalproject.dto.response.professor.ResponseProfessorDeleteById;
import com.epiis.finalproject.dto.response.professor.ResponseProfessorGetAll;
import com.epiis.finalproject.dto.response.professor.ResponseProfessorGetById;
import com.epiis.finalproject.dto.response.professor.ResponseProfessorInsert;
import com.epiis.finalproject.dto.response.professor.ResponseProfessorUpdate;
import com.epiis.finalproject.dto.response.user.ResponseUserUpdatePassword;
import com.epiis.finalproject.entity.EntityProfessor;
import com.epiis.finalproject.entity.EntityRole;
import com.epiis.finalproject.entity.EntityUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.epiis.finalproject.helper.PasswordUpdateHelper;
import com.epiis.finalproject.repository.RepositoryProfessor;
import com.epiis.finalproject.repository.RepositoryRole;
import com.epiis.finalproject.repository.RepositoryUser;
import jakarta.persistence.EntityManager;
import com.epiis.finalproject.staticdata.EnumRoles;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BusinessProfessor {
	private static final Logger log = LoggerFactory.getLogger(BusinessProfessor.class);
	private final RepositoryProfessor repositoryProfessor;
	private final RepositoryUser repositoryUser;
	private final PasswordEncoder passwordEncoder;
	private final RepositoryRole repositoryRole;
	private final EntityManager entityManager;
	private final PasswordUpdateHelper passwordUpdateHelper;
	
	public BusinessProfessor(RepositoryProfessor repositoryProfessor, RepositoryUser repositoryUser, PasswordEncoder passwordEncoder, RepositoryRole repositoryRole, EntityManager entityManager, PasswordUpdateHelper passwordUpdateHelper) {
		this.repositoryProfessor = repositoryProfessor;
		this.repositoryUser = repositoryUser;
		this.passwordEncoder = passwordEncoder;
		this.repositoryRole = repositoryRole;
		this.entityManager = entityManager;
		this.passwordUpdateHelper = passwordUpdateHelper;
	}
	
	@Transactional
	public ResponseProfessorInsert insert(RequestProfessorInsert request) {
		ResponseProfessorInsert response = new ResponseProfessorInsert();
		try {
			EntityRole roleProfessor = repositoryRole.findByNameRole(EnumRoles.PROFESSOR.toString()).orElse(null);
			if (roleProfessor == null) {
				response.error();
				response.getListMessage().add("Error interno: El rol PROFESSOR no está configurado en la base de datos.");
				return response;
			}
			
			if (repositoryUser.findByEmail(request.getEmail()).isPresent()) {
				response.error();
				response.getListMessage().add("El correo electrónico ya está registrado.");
				return response;
			}

			EntityUser entityUser = UserMutationHelper.buildEntityUser(
					request.getFirstName(), request.getSurName(), request.getEmail(), request.getPassword(), passwordEncoder);
			
			EntityProfessor entityProfessor = new EntityProfessor();
			entityProfessor.setIdProfessor(entityUser.getIdUser());
			entityProfessor.setParentUser(entityUser);
			entityProfessor.setCreatedAt(entityUser.getCreatedAt());
			entityProfessor.setUpdatedAt(entityUser.getCreatedAt());
			
			entityUser.setChildProfessor(entityProfessor); // Bidirectional association
			entityUser.setParentRole(roleProfessor); 
			
			entityManager.persist(entityUser); // Forces SQL INSERT for both User and Professor due to cascade
			
			response.success();
			response.getListMessage().add("Profesor registrado correctamente.");
			
		} catch (Exception e) {
			log.error("Error al registrar el profesor", e);
			response.error();
			response.getListMessage().add("Error al registrar el profesor: " + e.getMessage());
		}
		
		return response;
	}
	
	public Map<String, Object> getAll(){
		return ResponseMapBuilder.buildDataMap(
				new ResponseProfessorGetAll(),
				"Profesores entregados correctamente",
				repositoryProfessor.findAll());
	}
	
	public Map<String, Object> getById(String idProfessor){
		return ResponseMapBuilder.buildDataMap(
				new ResponseProfessorGetById(),
				"Profesor encontrado exitosamente",
				repositoryUser.findById(idProfessor));
	}
	
	public ResponseProfessorDeleteById deleteById(String idProfessor) {
		return UserMutationHelper.deleteById(
				new ResponseProfessorDeleteById(), idProfessor,
				repositoryProfessor::deleteById,
				"Profesor eliminado correctamente");
	}
	
	public ResponseProfessorUpdate update(String idUser, RequestProfessorUpdate request) {
		return UserMutationHelper.updateUser(
				repositoryUser, new ResponseProfessorUpdate(), idUser,
				entityUser -> {
					entityUser.setFirstName(request.getFirstName());
					entityUser.setSurName(request.getSurName());
					entityUser.setEmail(request.getEmail());
					entityUser.setUpdatedAt(DateUtil.currentSqlDate());

					repositoryUser.save(entityUser);

					EntityRole entityRole = new EntityRole();

					entityRole.setIdRole(request.getIdRole());

					if ("Professor".equals(entityRole.getNameRole())) {
						EntityProfessor entityProfessor = new EntityProfessor();

						entityProfessor.setIdProfessor(UUID.randomUUID().toString());
						entityProfessor.setParentUser(entityUser);
						entityProfessor.setCreatedAt(entityUser.getCreatedAt());
						entityProfessor.setUpdatedAt(entityUser.getCreatedAt());

						repositoryProfessor.save(entityProfessor);
					}
				},
				"Profesor actualizado correctamente",
				"Error el profesor no se actualizo");
	}
	
	public ResponseUserUpdatePassword updatePassword(String email, RequestUserUpdatePassword request) {
		return passwordUpdateHelper.updatePassword(
				email, request,
				"Contraseña de profesor actualizada correctamente",
				"Contraseña de profesor no actualizada");
	}
	
	public List<Map<String, Object>> searchProfessorsForAutocomplete(String query) {
	    if (query == null || query.trim().isEmpty()) {
	        return Collections.emptyList();
	    }
	    
	    List<EntityUser> professors = repositoryUser.searchProfessorsByTerm(query.trim(), PageRequest.of(0, 10));
	    
	    return professors.stream().map(prof -> {
	        Map<String, Object> map = new HashMap<>();
	        map.put("idProfessor", prof.getIdUser());
	        map.put("fullName", prof.getSurName() + " " + prof.getFirstName());
	        return map;
	    }).toList();
	}
}
