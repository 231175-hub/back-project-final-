package com.epiis.finalproject.business;

import java.util.List;
import java.util.Map;

import com.epiis.finalproject.helper.DateUtil;
import com.epiis.finalproject.helper.ResponseMapBuilder;
import com.epiis.finalproject.helper.UserMutationHelper;

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
import com.epiis.finalproject.helper.PasswordUpdateHelper;
import com.epiis.finalproject.staticdata.EnumRoles;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BusinessUser {
	private static final Logger log = LoggerFactory.getLogger(BusinessUser.class);
	private final RepositoryUser repositoryUser;
	private final RepositoryRole repositoryRole;
	private final PasswordEncoder passwordEncoder;
	private final PasswordUpdateHelper passwordUpdateHelper;

	public BusinessUser(RepositoryUser repositoryUser, RepositoryRole repositoryRole, PasswordEncoder passwordEncoder, PasswordUpdateHelper passwordUpdateHelper) {
		this.repositoryUser = repositoryUser;
		this.repositoryRole = repositoryRole;
		this.passwordEncoder = passwordEncoder;
		this.passwordUpdateHelper = passwordUpdateHelper;
	}

	@Transactional
	public ResponseUserInsert insert(RequestUserInsert request) {
		ResponseUserInsert response = new ResponseUserInsert();

		try {
			EntityRole roleAdmin = repositoryRole.findByNameRole(EnumRoles.ADMIN.toString())
					.orElseThrow(() -> new IllegalStateException(
							"Error interno: El rol ADMIN no está configurado en la BD."));

			if (repositoryUser.findByEmail(request.getEmail()).isPresent()) {
				throw new IllegalArgumentException("El correo electrónico ya está registrado.");
			}

			EntityUser entityUser = UserMutationHelper.buildEntityUser(
					request.getFirstName(), request.getSurName(), request.getEmail(), request.getPassword(), passwordEncoder);

			entityUser.setParentRole(roleAdmin);

			repositoryUser.save(entityUser);

			response.success();
			response.getListMessage().add("Administrador registrado correctamente.");

		} catch (Exception e) {
			log.error("Error al registrar el administrador", e);
			response.error();
			response.getListMessage().add("Error al registrar el administrador: " + e.getMessage());
		}

		return response;
	}

	public Map<String, Object> getAll() {
		return ResponseMapBuilder.buildDataMap(
				new ResponseUserGetAll(),
				"Usuarios entregados correctamente",
				repositoryUser.findAll().stream()
						.filter(user -> user.getParentRole() != null
								&& "Admin".equalsIgnoreCase(user.getParentRole().getNameRole()))
						.toList());
	}

	public Map<String, Object> getById(String idUser) {
		return ResponseMapBuilder.buildDataMap(
				new ResponseUserGetById(),
				"Usuario encontrado exitosamente",
				repositoryUser.findById(idUser));
	}

	public ResponseUserDeleteById deleteById(String idUser) {
		return UserMutationHelper.deleteById(
				new ResponseUserDeleteById(), idUser,
				repositoryUser::deleteById,
				"Usuario eliminado correctamente");
	}

	public ResponseUserUpdate update(String idUser, RequestUserUpdate request) {
		return UserMutationHelper.updateUser(
				repositoryUser, new ResponseUserUpdate(), idUser,
				entityUser -> {
					entityUser.setFirstName(request.getFirstName());
					entityUser.setSurName(request.getSurName());
					entityUser.setEmail(request.getEmail());
					entityUser.setUpdatedAt(DateUtil.currentSqlDate());
					repositoryUser.save(entityUser);
				},
				"Usuario actualizado correctamente",
				"Error el usuario no se actualizo");
	}

	public ResponseUserUpdatePassword updatePassword(String email, RequestUserUpdatePassword request) {
		return passwordUpdateHelper.updatePassword(
				email, request,
				"Contraseña de usuario actualizada correctamente",
				"Contraseña de usuario no actualizada");
	}

	public void updateProfileImage(String idUser, RequestUserUpdateUploadImg request) throws java.io.IOException {
		EntityUser user = repositoryUser.findById(idUser)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads/profile/");
		if (!java.nio.file.Files.exists(uploadPath)) {
			java.nio.file.Files.createDirectories(uploadPath);
		}

		String fileName = idUser + ".jpg";
		java.nio.file.Files.copy(request.getFile().getInputStream(), uploadPath.resolve(fileName),
				java.nio.file.StandardCopyOption.REPLACE_EXISTING);

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
