package com.epiis.finalproject.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epiis.finalproject.business.BusinessUser;
import com.epiis.finalproject.entity.EntityUser;
import com.epiis.finalproject.repository.RepositoryUser;

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
	
	@Transactional(readOnly = true)
	@GetMapping(path = "me")
	public Map<String, Object> ObtainMyProfile() {
		Map<String, Object> profile = new HashMap<>();
		
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !authentication.isAuthenticated()) {
				profile.put("error", "Usuario no autenticado");
				return profile;
			}

			String userEmail = authentication.getName();
			EntityUser localuser = repositoryUser.findFirstByEmail(userEmail)
					.orElseGet(() -> repositoryUser.findById(userEmail).orElse(null));
			
			if (localuser != null) {
				profile.put("id_user", localuser.getIdUser());
				profile.put("email", localuser.getEmail());
				profile.put("full_name", (localuser.getFirstName() != null ? localuser.getFirstName() : "") + " " + (localuser.getSurName() != null ? localuser.getSurName() : ""));
				profile.put("url_image", localuser.getUrlImageProfile());
				
				if (localuser.getParentRole() != null && localuser.getParentRole().getNameRole() != null) {
					profile.put("role", localuser.getParentRole().getNameRole());
				} else {
					profile.put("role", "Sin Rol");
				}
			} else {
				profile.put("error", "No se pudo cargar el perfil local del usuario.");
				profile.put("email", userEmail);
			}
		} catch (Exception e) {
			e.printStackTrace();
			profile.put("error", "Error interno al cargar perfil: " + e.getMessage());
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
