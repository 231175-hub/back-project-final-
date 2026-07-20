package com.epiis.finalproject.business;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
import com.epiis.finalproject.repository.RepositoryProfessor;
import com.epiis.finalproject.repository.RepositoryRole;
import com.epiis.finalproject.repository.RepositoryUser;
import jakarta.persistence.EntityManager;
import com.epiis.finalproject.staticdata.EnumRoles;

import jakarta.transaction.Transactional;

@Service
public class BusinessProfessor {
	private final RepositoryProfessor repositoryProfessor;
	private final RepositoryUser repositoryUser;
	private final PasswordEncoder passwordEncoder;
	private final RepositoryRole repositoryRole;
	private final EntityManager entityManager;
	
	public BusinessProfessor(RepositoryProfessor repositoryProfessor, RepositoryUser repositoryUser, PasswordEncoder passwordEncoder, RepositoryRole repositoryRole, EntityManager entityManager) {
		this.repositoryProfessor = repositoryProfessor;
		this.repositoryUser = repositoryUser;
		this.passwordEncoder = passwordEncoder;
		this.repositoryRole = repositoryRole;
		this.entityManager = entityManager;
	}
	
	@Transactional
	public ResponseProfessorInsert insert(RequestProfessorInsert request) {
		ResponseProfessorInsert response = new ResponseProfessorInsert();
		try {
			EntityRole roleProfessor = repositoryRole.findByNameRole(EnumRoles.PROFESSOR.toString())
					.orElseThrow(() -> new RuntimeException("Error interno: El rol PROFESSOR no está configurado en la base de datos."));
			
			if (repositoryUser.findByEmail(request.getEmail()).isPresent()) {
				throw new RuntimeException("El correo electrónico ya está registrado.");
			}

			String userId = UUID.randomUUID().toString();
		
			EntityUser entityUser = new EntityUser();
			entityUser.setIdUser(userId);
			entityUser.setFirstName(request.getFirstName());
			entityUser.setSurName(request.getSurName());
			entityUser.setEmail(request.getEmail());
			entityUser.setPassword(passwordEncoder.encode(request.getPassword()));
			entityUser.setCreatedAt(new java.sql.Date(new Date().getTime()));
			entityUser.setUpdatedAt(entityUser.getCreatedAt());
			
			EntityProfessor entityProfessor = new EntityProfessor();
			entityProfessor.setIdProfessor(userId);
			entityProfessor.setParentUser(entityUser);
			entityProfessor.setCreatedAt(entityUser.getCreatedAt());
			entityProfessor.setUpdatedAt(entityUser.getCreatedAt());
			
			entityUser.setChildProfessor(entityProfessor); // Bidirectional association
			entityUser.setParentRole(roleProfessor); 
			
			entityManager.persist(entityUser); // Forces SQL INSERT for both User and Professor due to cascade
			
			response.success();
			response.getListMessage().add("Profesor registrado correctamente.");
			
		} catch (Exception e) {
			e.printStackTrace();
			response.error();
			response.getListMessage().add("Error al registrar el profesor: " + e.getMessage());
		}
		
		return response;
	}
	
	public Map<String, Object> getAll(){
		ResponseProfessorGetAll response = new ResponseProfessorGetAll();
		
		List<EntityProfessor> entityProfessor = repositoryProfessor.findAll();
		
		Map<String, Object> res = new HashMap<>();
		
		response.success();
		response.getListMessage().add("Profesores entregados correctamente");
		
		res.put("message", response);
		res.put("data", entityProfessor);
		
		return res;
	}
	
	public Map<String, Object> getById(String idProfessor){
		ResponseProfessorGetById response = new ResponseProfessorGetById();
		
		Map<String, Object> res = new HashMap<>();
		
		Optional<EntityUser> entity = repositoryUser.findById(idProfessor);
		
		response.success();
		response.getListMessage().add("Profesor encontrado exitosamente");
		
		res.put("message", response);
		res.put("data", entity);
		
		return res;
	}
	
	public ResponseProfessorDeleteById deleteById(String idProfessor) {
		ResponseProfessorDeleteById response = new ResponseProfessorDeleteById();
		
		repositoryProfessor.deleteById(idProfessor);
		
		response.success();
		response.getListMessage().add("Profesor eliminado correctamente");
		
		return response;
	}
	
	public ResponseProfessorUpdate update(String idUser, RequestProfessorUpdate request) {
		ResponseProfessorUpdate response = new ResponseProfessorUpdate();
		
		Optional<EntityUser> optional = repositoryUser.findById(idUser);
		
		if (optional.isPresent()) {
			EntityUser entityUser = optional.get();
			
			entityUser.setFirstName(request.getFirstName());
			entityUser.setSurName(request.getSurName());
			entityUser.setEmail(request.getEmail());
			entityUser.setUpdatedAt(new java.sql.Date(new Date().getTime()));
			
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
			
			response.success();
			response.getListMessage().add("Profesor actualizado correctamente");
			
			return response;
		}
		
		response.error();
		response.getListMessage().add("Error el profesor no se actualizo");
		
		return response;
	}
	
	public ResponseUserUpdatePassword updatePassword(String email, RequestUserUpdatePassword request) {
		ResponseUserUpdatePassword response = new ResponseUserUpdatePassword();
		
		Optional<EntityUser> optional = repositoryUser.findByEmail(email);
		
		if (optional.isPresent()) {
			EntityUser entityUser = optional.get();
			
			entityUser.setUpdatedAt(new java.sql.Date(new Date().getTime()));
			
			response.success();
			response.getListMessage().add("Contraseña de profesor actualizada correctamente");
		}
		
		response.error();
		response.getListMessage().add("Contraseña de profesor no actualizada");
		
		return response;
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
	    }).collect(Collectors.toList());
	}
}
