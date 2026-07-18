package com.epiis.finalproject.controller;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epiis.finalproject.business.BusinessUser;
import com.epiis.finalproject.entity.EntityUser;
import com.epiis.finalproject.repository.RepositoryUser;
import org.springframework.core.io.Resource;

@PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'STUDENT')")
@RestController
@RequestMapping(path = "intranet")
public class MeController {
	private final RepositoryUser repositoryUser;
	private final BusinessUser businessUser;
	
	public MeController(RepositoryUser repositoryUser, BusinessUser businessUser) {
		this.repositoryUser = repositoryUser;
		this.businessUser = businessUser;
	}
	
	@GetMapping(path = "me")
	public Map<String, Object> ObtainMyProfile(@AuthenticationPrincipal Jwt jwt){
		Map<String, Object> profile = new HashMap<>();
		
		String keyCloakId = jwt.getSubject();
		
		try {
			EntityUser localuser = repositoryUser.findById(keyCloakId)
					.orElseThrow(() -> new RuntimeException("Usuario no encontrado en la base de datos local"));
			
			profile.put("ïd_user", localuser.getIdUser());
			profile.put("email", localuser.getEmail());
			profile.put("full_name", localuser.getFirstName() + " " + localuser.getSurName());
			profile.put("url_image", localuser.getUrlImageProfile());
			
			if (localuser.getParentRole() != null) {
				profile.put("role", localuser.getParentRole().getNameRole());
			} else {
				profile.put("role", "Sin Rol");
			}
		} catch (Exception e) {
			profile.put("error", "No se pudo cargar el perfil local: " + e.getMessage());
			profile.put("id_keycloak", keyCloakId);
			profile.put("email", jwt.getClaimAsString("email"));
		}

		return profile;
	}
	
	@GetMapping(path = "me/image/{filename:.+}")
	public ResponseEntity<Resource> serveProfileImage(@PathVariable String filename) {
		Resource resource = businessUser.getProfilePictureResource(filename);
		return ResponseEntity.ok()
				.contentType(MediaType.IMAGE_JPEG)
				.body(resource);
	}
}
