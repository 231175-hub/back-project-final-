package com.epiis.finalproject.integration;

import java.util.Collections;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;

@Service
public class KeycloakAdminService {
	
	@Value("${keycloak.admin.server-url}")
	private String serverUrl;
	
	@Value("${keycloak.target.realm}")
	private String targetRealm;
	
	@Value("${keycloak.admin.client-id}")
	private String clientId;
	
	@Value("${keycloak.admin.username}")
	private String username;
	
	@Value("${keycloak.admin.password}")
	private String password;
	
	public String createUser(String email, String firstName, String surName, String userPassword, String idRole) {
		Keycloak keycloak = KeycloakBuilder.builder()
				.serverUrl(serverUrl)
				.realm("master")
				.clientId(clientId)
				.username(username)
				.password(password)
				.build();
		
		UserRepresentation user = new UserRepresentation();
		user.setUsername(email);
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setLastName(surName);
		user.setEnabled(true);
		
		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setTemporary(false);
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue(userPassword);
		user.setCredentials(Collections.singletonList(credential));
		
		RealmResource realmResource = keycloak.realm(targetRealm);
		UsersResource usersResource = realmResource.users();
		
		Response response = usersResource.create(user);
		
		if (response.getStatus() == 201) {
			String path = response.getLocation().getPath();
			String userId = path.substring(path.lastIndexOf('/') +1);
			
			try {
				String keycloakRole = idRole;
				if ("Profesor".equalsIgnoreCase(idRole)) {
					keycloakRole = "PROFESSOR";
				} else if ("Estudiante".equalsIgnoreCase(idRole)) {
					keycloakRole = "STUDENT";
				} else if ("Admin".equalsIgnoreCase(idRole)) {
					keycloakRole = "Admin";
				}

				RoleRepresentation roleRepresentation = realmResource.roles().get(keycloakRole).toRepresentation();
				
				realmResource.users().get(userId)
						.roles()
						.realmLevel()
						.add(Collections.singletonList(roleRepresentation));
				
				System.out.println("Éxito: Rol " + keycloakRole + " asignado automáticamente al usuario " + email);
			} catch (Exception e) {
				System.err.println("El usuario se creó, pero falló la asignación del rol: " + e.getMessage());
				try {
					System.err.println("Roles disponibles en el Realm de Keycloak:");
					realmResource.roles().list().forEach(r -> System.err.println(" - " + r.getName()));
				} catch (Exception ex) {
					System.err.println("No se pudo listar los roles de Keycloak: " + ex.getMessage());
				}
				throw new RuntimeException("Error al asignar rol en Keycloak: " + e.getMessage());
			}
			
			return userId;
		} else if (response.getStatus() == 409) {
			throw new RuntimeException("El correo electrónico ya está registrado en Keycloak.");
		} else {
			throw new RuntimeException("Error al crear usuario en Keycloak, Código de estado: " + response.getStatus());
		}
	}

	public void deleteUser(String userId) {
		Keycloak keycloak = KeycloakBuilder.builder()
				.serverUrl(serverUrl)
				.realm("master")
				.clientId(clientId)
				.username(username)
				.password(password)
				.build();
		try {
			RealmResource realmResource = keycloak.realm(targetRealm);
			UsersResource usersResource = realmResource.users();
			usersResource.delete(userId);
			System.out.println("Éxito: Usuario " + userId + " eliminado de Keycloak por rollback.");
		} catch (Exception e) {
			System.err.println("Falló la eliminación del usuario en Keycloak durante el rollback: " + e.getMessage());
		}
	}
}
