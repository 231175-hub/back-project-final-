package com.epiis.finalproject.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.epiis.finalproject.dto.request.user.RequestUserInsert;
import com.epiis.finalproject.dto.request.user.RequestUserUpdate;
import com.epiis.finalproject.dto.request.user.RequestUserUpdatePassword;
import com.epiis.finalproject.dto.request.user.RequestUserUpdateUploadImg;
import com.epiis.finalproject.dto.response.user.ResponseUserDeleteById;
import com.epiis.finalproject.dto.response.user.ResponseUserGetAll;
import com.epiis.finalproject.dto.response.user.ResponseUserGetById;
import com.epiis.finalproject.dto.response.user.ResponseUserInsert;
import com.epiis.finalproject.dto.response.user.ResponseUserUpdate;
import com.epiis.finalproject.dto.response.user.ResponseUserUpdatePassword;
import com.epiis.finalproject.entity.EntityRole;
import com.epiis.finalproject.entity.EntityUser;
import com.epiis.finalproject.repository.RepositoryRole;
import com.epiis.finalproject.repository.RepositoryUser;
import com.epiis.finalproject.staticdata.EnumRoles;

import jakarta.transaction.Transactional;

@Service
public class BusinessUser {
	private final RepositoryUser repositoryUser;
	private final RepositoryRole repositoryRole;
	private final PasswordEncoder passwordEncoder;
	
	public BusinessUser(RepositoryUser repositoryUser, RepositoryRole repositoryRole, PasswordEncoder passwordEncoder) {
		this.repositoryUser = repositoryUser;
		this.repositoryRole = repositoryRole;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional
	public ResponseUserInsert insert(RequestUserInsert request) {
		ResponseUserInsert response = new ResponseUserInsert();
	    
	    try {
	        EntityRole roleAdmin = repositoryRole.findByNameRole(EnumRoles.ADMIN.toString())
	        		.orElseThrow(() -> new RuntimeException("Error interno: El rol ADMIN no está configurado en la BD."));

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
	        
	        entityUser.setParentRole(roleAdmin);
	        
	        entityUser.setCreatedAt(new java.sql.Date(new Date().getTime()));
	        entityUser.setUpdatedAt(entityUser.getCreatedAt());
	        
	        repositoryUser.save(entityUser);
	        
	        response.success();
	        response.getListMessage().add("Administrador registrado correctamente.");
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.error();
	        response.getListMessage().add("Error al registrar el administrador: " + e.getMessage());
	    }
	    
	    return response;
	}
	
	public Map<String, Object> getAll(){
		ResponseUserGetAll response = new ResponseUserGetAll();
		
		List<EntityUser> entityUser = repositoryUser.findAll().stream()
				.filter(user -> user.getParentRole() != null && "Admin".equalsIgnoreCase(user.getParentRole().getNameRole()))
				.collect(Collectors.toList());
		
		Map<String, Object> res = new HashMap<>();
		
		response.success();
		response.getListMessage().add("Usuarios entregados correctamente");
		
		res.put("message", response);
		res.put("data", entityUser);
		
		return res;
	}
	
	public Map<String, Object> getById(String idUser){
		ResponseUserGetById response = new ResponseUserGetById();
		
		Map<String, Object> res = new HashMap<>();
		
		Optional<EntityUser> entity = repositoryUser.findById(idUser);
		
		response.success();
		response.getListMessage().add("Usuario encontrado exitosamente");
		
		res.put("message", response);
		res.put("data", entity);
		
		return res;
	}
	
	public ResponseUserDeleteById deleteById(String idUser) {
		ResponseUserDeleteById response = new ResponseUserDeleteById();
		
		repositoryUser.deleteById(idUser);
		
		response.success();
		response.getListMessage().add("Usuario eliminado correctamente");
		
		return response;
	}
	
	public ResponseUserUpdate update(String idUser, RequestUserUpdate request) {
		ResponseUserUpdate response = new ResponseUserUpdate();
		
		Optional<EntityUser> optional = repositoryUser.findById(idUser);
		
		if (optional.isPresent()) {
			EntityUser entityUser = optional.get();
			
			entityUser.setFirstName(request.getFirstName());
			entityUser.setSurName(request.getSurName());
			entityUser.setEmail(request.getEmail());
			entityUser.setUpdatedAt(new java.sql.Date(new Date().getTime()));
			
			repositoryUser.save(entityUser);
			
			response.success();
			response.getListMessage().add("Usuario actualizado correctamente");
			
			return response;
		}
		
		response.error();
		response.getListMessage().add("Error el usuario no se actualizo");
		
		return response;
	}
	
	public ResponseUserUpdatePassword updatePassword(String email, RequestUserUpdatePassword request) {
		ResponseUserUpdatePassword response = new ResponseUserUpdatePassword();
		
		Optional<EntityUser> optional = repositoryUser.findByEmail(email);
		
		if (optional.isPresent()) {
			EntityUser entityUser = optional.get();
			entityUser.setPassword(passwordEncoder.encode(request.getPassword()));
			entityUser.setUpdatedAt(new java.sql.Date(new Date().getTime()));
			repositoryUser.save(entityUser);
			
			response.success();
			response.getListMessage().add("Contraseña de usuario actualizada correctamente");
			return response;
		}
		
		response.error();
		response.getListMessage().add("Contraseña de usuario no actualizada");
		
		return response;
	}
	
	public void updateProfileImage(String idUser, RequestUserUpdateUploadImg request) throws java.io.IOException {
		EntityUser user = repositoryUser.findById(idUser)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads/profile/");
		if (!java.nio.file.Files.exists(uploadPath)) {
			java.nio.file.Files.createDirectories(uploadPath);
		}

		String fileName = idUser + ".jpg";
		java.nio.file.Files.copy(request.getFile().getInputStream(), uploadPath.resolve(fileName), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

		user.setUrlImageProfile("/intranet/me/image/" + fileName);
		repositoryUser.save(user);
	}
	
	public org.springframework.core.io.Resource getProfilePictureResource(String filename) {
		java.nio.file.Path filePath = java.nio.file.Paths.get("uploads/profile/").resolve(filename);
		if (java.nio.file.Files.exists(filePath)) {
			try { 
				return new org.springframework.core.io.UrlResource(filePath.toUri()); 
			} catch (Exception e) { 
				return null; 
			}
		}
		return new org.springframework.core.io.ClassPathResource("static/img/default-avatar.png");
	}
}
