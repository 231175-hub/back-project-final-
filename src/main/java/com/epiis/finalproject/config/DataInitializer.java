package com.epiis.finalproject.config;

import com.epiis.finalproject.repository.RepositoryUser;
import com.epiis.finalproject.staticdata.EnumRoles;

import java.util.Date;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Lazy;
import com.epiis.finalproject.entity.EntityRole;
import com.epiis.finalproject.entity.EntityUser;
import com.epiis.finalproject.integration.KeycloakAdminService;
import com.epiis.finalproject.repository.RepositoryRole;

@Configuration
public class DataInitializer {
	DataInitializer(RepositoryUser repositoryUser) {
	}

	@Bean
	CommandLineRunner initDatabase(
			RepositoryRole repositoryRole,
			RepositoryUser repositoryUser,
			@Lazy KeycloakAdminService keycloakAdminService) {
		
		return args -> {
			if (repositoryRole.count() == 0) {
				System.out.println("Seeder: Insertando roles Iniciales...");
				
				EntityRole adminRole = new EntityRole();
				adminRole.setIdRole(UUID.randomUUID().toString());
				adminRole.setNameRole(EnumRoles.ADMIN.toString());
				adminRole.setCreatedAt(new java.sql.Date(new Date().getTime()));
				adminRole.setUpdatedAt(adminRole.getCreatedAt());
				
				repositoryRole.save(adminRole);
				
				EntityRole professorRole = new EntityRole();
				professorRole.setIdRole(UUID.randomUUID().toString());
				professorRole.setNameRole(EnumRoles.PROFESSOR.toString());
				professorRole.setCreatedAt(new java.sql.Date(new Date().getTime()));
				professorRole.setUpdatedAt(professorRole.getCreatedAt());
				
				repositoryRole.save(professorRole);
				
				EntityRole studentRole = new EntityRole();
				studentRole.setIdRole(UUID.randomUUID().toString());
				studentRole.setNameRole(EnumRoles.STUDENT.toString());
				studentRole.setCreatedAt(new java.sql.Date(new Date().getTime()));
				studentRole.setUpdatedAt(studentRole.getCreatedAt());
				
				repositoryRole.save(studentRole);
			}
			
			if (repositoryUser.count() == 0) {
				System.out.println("Seeder: No se encontraron usuarios. Creando Administrador Maestro...");
				
				String masterEmail = "admin@master.edu.pe";
				String masterPassword = "adminmaster12345678";
				
				try {
					String keycloakUserId = keycloakAdminService.createUser(
							masterEmail,
							"Administrator",
							"Master",
							masterPassword, 
							"Admin"
					);
					
					EntityRole roleAdmin = repositoryRole.findByNameRole("Admin")
							.orElseThrow(() -> new RuntimeException("Error: El rol Admin no existe."));
					
					EntityUser adminUser = new EntityUser();
					adminUser.setIdUser(keycloakUserId);
					adminUser.setFirstName("Administrator");
					adminUser.setSurName("Master");
					adminUser.setEmail(masterEmail);
					adminUser.setParentRole(roleAdmin);
					adminUser.setCreatedAt(new java.sql.Date(new Date().getTime()));
					adminUser.setUpdatedAt(adminUser.getCreatedAt());
					
					repositoryUser.save(adminUser);
					
					System.out.println("ADMINISTRADOR MAESTRO CREADO EXITOSAMENTE");
					System.out.println("Usuario: " + masterEmail);
					System.out.println("Contraseña: " + masterPassword);
					System.out.println("UUID Keycloak: " + keycloakUserId);
				} catch (Throwable e) {
					System.out.println("Error critico en el seeder al crear el Admin: " + e.getMessage());
					e.printStackTrace();
				}
			}
		};
	}
}
