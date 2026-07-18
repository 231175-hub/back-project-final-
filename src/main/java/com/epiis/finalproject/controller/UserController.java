package com.epiis.finalproject.controller;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;


import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epiis.finalproject.business.BusinessUser;
import com.epiis.finalproject.dto.request.user.RequestUserInsert;
import com.epiis.finalproject.dto.request.user.RequestUserUpdate;
import com.epiis.finalproject.dto.request.user.RequestUserUpdatePassword;
import com.epiis.finalproject.dto.request.user.RequestUserUpdateUploadImg;
import com.epiis.finalproject.dto.response.user.ResponseUserDeleteById;
import com.epiis.finalproject.dto.response.user.ResponseUserInsert;
import com.epiis.finalproject.dto.response.user.ResponseUserUpdate;
import com.epiis.finalproject.dto.response.user.ResponseUserUpdatePassword;

@PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'STUDENT')")
@RestController
@RequestMapping(path = "intranet")
public class UserController {
	private final BusinessUser businessUser;

	public UserController(BusinessUser businessUser) {
		this.businessUser = businessUser;
	}
	
	@PostMapping(path = "registeruser", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseUserInsert> insert(@Valid @RequestBody RequestUserInsert request){
		ResponseUserInsert response = businessUser.insert(request);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(path = "indexuser")
	public ResponseEntity<Map<String, Object>> getAll(){
		
		return ResponseEntity.ok(businessUser.getAll());
	}
	
	@GetMapping(path = "showuser/{idUser}")
	public ResponseEntity<Map<String, Object>> getById(@PathVariable String idUser){
		
		return ResponseEntity.ok(businessUser.getById(idUser));
	}
	
	@DeleteMapping(path = "deleteuser/{idUser}")
	public ResponseEntity<ResponseUserDeleteById> deleteById(@PathVariable String idUser){
		ResponseUserDeleteById response = businessUser.deleteById(idUser);
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping(path = "updateuser/{idUser}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseUserUpdate> update(@PathVariable String idUser, @Valid @RequestBody RequestUserUpdate request){
		ResponseUserUpdate response = businessUser.update(idUser, request);
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping(path = "updatepassworduser/{email}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseUserUpdatePassword> updatePassword(@PathVariable String email ,@Valid @RequestBody RequestUserUpdatePassword request){
		ResponseUserUpdatePassword response = businessUser.updatePassword(email, request);
		
		return ResponseEntity.ok(response);
	}
	
	@PostMapping(path = "uploadimg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadMyProfileImage(@AuthenticationPrincipal Jwt jwt, @ModelAttribute RequestUserUpdateUploadImg request) {
		try {
			String idUser = jwt.getSubject();
			businessUser.updateProfileImage(idUser, request);
			return ResponseEntity.ok("{\"message\": \"Foto de perfil actualizada correctamente\"}");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("{\"error\": \"Error al guardar la foto\"}");
		}
	}
}
