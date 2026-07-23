package com.epiis.finalproject.config;

import com.epiis.finalproject.entity.EntityRole;
import com.epiis.finalproject.entity.EntityUser;
import com.epiis.finalproject.repository.RepositoryRole;
import com.epiis.finalproject.repository.RepositoryUser;
import com.epiis.finalproject.staticdata.EnumRoles;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
	DataInitializer() {
	}

	@Bean
	CommandLineRunner initDatabase(
			RepositoryRole repositoryRole,
			RepositoryUser repositoryUser,
			PasswordEncoder passwordEncoder,
			JdbcTemplate jdbcTemplate) {
		
		return args -> {
			// Fix potential duplicate column constraints caused by Hibernate ddl-auto evolution
			try { jdbcTemplate.execute("ALTER TABLE tuser MODIFY idUser VARCHAR(255) NULL"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("ALTER TABLE tuser MODIFY id_user VARCHAR(255) NULL"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("ALTER TABLE trole MODIFY idRole VARCHAR(255) NULL"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("ALTER TABLE trole MODIFY id_role VARCHAR(255) NULL"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("ALTER TABLE tprofessor MODIFY idProfessor VARCHAR(255) NULL"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("ALTER TABLE tprofessor MODIFY id_professor VARCHAR(255) NULL"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("ALTER TABLE tstudent MODIFY idStudent VARCHAR(255) NULL"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("ALTER TABLE tstudent MODIFY id_student VARCHAR(255) NULL"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("ALTER TABLE tschool MODIFY idSchool VARCHAR(255) NULL"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("ALTER TABLE tschool MODIFY id_school VARCHAR(255) NULL"); } catch (Exception ignored) {}

			// Synchronize data between camelCase and snake_case columns for existing legacy records
			// 1. tschool
			try { jdbcTemplate.execute("UPDATE tschool SET id_school = idSchool WHERE (id_school IS NULL OR id_school = '') AND idSchool IS NOT NULL AND idSchool != ''"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("UPDATE tschool SET idSchool = id_school WHERE (idSchool IS NULL OR idSchool = '') AND id_school IS NOT NULL AND id_school != ''"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("UPDATE tschool SET name_school = nameSchool WHERE (name_school IS NULL OR name_school = '') AND nameSchool IS NOT NULL AND nameSchool != ''"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("UPDATE tschool SET nameSchool = name_school WHERE (nameSchool IS NULL OR nameSchool = '') AND name_school IS NOT NULL AND name_school != ''"); } catch (Exception ignored) {}

			// 2. tuser
			try { jdbcTemplate.execute("UPDATE tuser SET id_user = idUser WHERE (id_user IS NULL OR id_user = '') AND idUser IS NOT NULL AND idUser != ''"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("UPDATE tuser SET idUser = id_user WHERE (idUser IS NULL OR idUser = '') AND id_user IS NOT NULL AND id_user != ''"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("UPDATE tuser SET first_name = firstName WHERE (first_name IS NULL OR first_name = '') AND firstName IS NOT NULL AND firstName != ''"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("UPDATE tuser SET firstName = first_name WHERE (firstName IS NULL OR firstName = '') AND first_name IS NOT NULL AND first_name != ''"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("UPDATE tuser SET sur_name = surName WHERE (sur_name IS NULL OR sur_name = '') AND surName IS NOT NULL AND surName != ''"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("UPDATE tuser SET surName = sur_name WHERE (surName IS NULL OR surName = '') AND sur_name IS NOT NULL AND sur_name != ''"); } catch (Exception ignored) {}

			// 3. trole
			try { jdbcTemplate.execute("UPDATE trole SET id_role = idRole WHERE (id_role IS NULL OR id_role = '') AND idRole IS NOT NULL AND idRole != ''"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("UPDATE trole SET idRole = id_role WHERE (idRole IS NULL OR idRole = '') AND id_role IS NOT NULL AND id_role != ''"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("UPDATE trole SET name_role = nameRole WHERE (name_role IS NULL OR name_role = '') AND nameRole IS NOT NULL AND nameRole != ''"); } catch (Exception ignored) {}
			try { jdbcTemplate.execute("UPDATE trole SET nameRole = name_role WHERE (nameRole IS NULL OR nameRole = '') AND name_role IS NOT NULL AND name_role != ''"); } catch (Exception ignored) {}

			// Assign default password '12345678' to legacy users in tuser if they have no password set
			try {
				String defaultHashedPassword = passwordEncoder.encode("12345678");
				jdbcTemplate.update("UPDATE tuser SET password = ? WHERE password IS NULL OR password = ''", defaultHashedPassword);
			} catch (Exception ignored) {}

			// Ensure initial roles exist safely
			List<EntityRole> existingRoles = repositoryRole.findAll();
			
			boolean hasAdminRole = existingRoles.stream().anyMatch(r -> r.getNameRole() != null && 
					(r.getNameRole().equalsIgnoreCase("ADMIN") || r.getNameRole().equalsIgnoreCase("Administrador")));
			if (!hasAdminRole) {
				try {
					EntityRole adminRole = new EntityRole();
					adminRole.setIdRole(UUID.randomUUID().toString());
					adminRole.setNameRole(EnumRoles.ADMIN.toString());
					adminRole.setCreatedAt(new java.sql.Date(new Date().getTime()));
					adminRole.setUpdatedAt(adminRole.getCreatedAt());
					repositoryRole.save(adminRole);
				} catch (Exception ignored) {}
			}

			boolean hasProfessorRole = existingRoles.stream().anyMatch(r -> r.getNameRole() != null && 
					(r.getNameRole().equalsIgnoreCase("PROFESSOR") || r.getNameRole().equalsIgnoreCase("Profesor") || r.getNameRole().equalsIgnoreCase("Docente")));
			if (!hasProfessorRole) {
				try {
					EntityRole professorRole = new EntityRole();
					professorRole.setIdRole(UUID.randomUUID().toString());
					professorRole.setNameRole(EnumRoles.PROFESSOR.toString());
					professorRole.setCreatedAt(new java.sql.Date(new Date().getTime()));
					professorRole.setUpdatedAt(professorRole.getCreatedAt());
					repositoryRole.save(professorRole);
				} catch (Exception ignored) {}
			}

			boolean hasStudentRole = existingRoles.stream().anyMatch(r -> r.getNameRole() != null && 
					(r.getNameRole().equalsIgnoreCase("STUDENT") || r.getNameRole().equalsIgnoreCase("Estudiante")));
			if (!hasStudentRole) {
				try {
					EntityRole studentRole = new EntityRole();
					studentRole.setIdRole(UUID.randomUUID().toString());
					studentRole.setNameRole(EnumRoles.STUDENT.toString());
					studentRole.setCreatedAt(new java.sql.Date(new Date().getTime()));
					studentRole.setUpdatedAt(studentRole.getCreatedAt());
					repositoryRole.save(studentRole);
				} catch (Exception ignored) {}
			}

			// Flexible lookup for Admin role
			EntityRole roleAdmin = repositoryRole.findAll().stream()
					.filter(r -> r.getNameRole() != null && 
							(r.getNameRole().equalsIgnoreCase("ADMIN") || 
							 r.getNameRole().equalsIgnoreCase("Admin") || 
							 r.getNameRole().equalsIgnoreCase("Administrador")))
					.findFirst()
					.orElseGet(() -> {
						EntityRole r = new EntityRole();
						r.setIdRole(UUID.randomUUID().toString());
						r.setNameRole(EnumRoles.ADMIN.toString());
						r.setCreatedAt(new java.sql.Date(new Date().getTime()));
						r.setUpdatedAt(r.getCreatedAt());
						return repositoryRole.save(r);
					});

			// Ensure Master Admin exists in the current database
			String masterEmail = "admin@master.edu.pe";
			String masterPassword = "adminmaster12345678";

			try {
				List<EntityUser> usersWithEmail = repositoryUser.findAll().stream()
						.filter(u -> masterEmail.equalsIgnoreCase(u.getEmail()))
						.toList();

				if (usersWithEmail.isEmpty()) {
					System.out.println("Seeder: Creando Administrador Maestro (" + masterEmail + ")...");
					EntityUser adminUser = new EntityUser();
					adminUser.setIdUser(UUID.randomUUID().toString());
					adminUser.setFirstName("Administrator");
					adminUser.setSurName("Master");
					adminUser.setEmail(masterEmail);
					adminUser.setPassword(passwordEncoder.encode(masterPassword));
					adminUser.setParentRole(roleAdmin);
					adminUser.setCreatedAt(new java.sql.Date(new Date().getTime()));
					adminUser.setUpdatedAt(adminUser.getCreatedAt());

					repositoryUser.save(adminUser);

					System.out.println("=========================================");
					System.out.println("ADMINISTRADOR MAESTRO CREADO EXITOSAMENTE");
					System.out.println("Usuario: " + masterEmail);
					System.out.println("Contraseña: " + masterPassword);
					System.out.println("=========================================");
				} else {
					// User exists, make sure password is set to BCrypt hashed masterPassword if needed
					EntityUser existingUser = usersWithEmail.get(0);
					existingUser.setPassword(passwordEncoder.encode(masterPassword));
					existingUser.setParentRole(roleAdmin);
					repositoryUser.save(existingUser);
					System.out.println("Seeder: El Administrador Maestro " + masterEmail + " fue actualizado con la clave maestra.");
				}
			} catch (Throwable e) {
				System.out.println("Error procesando Administrador Maestro: " + e.getMessage());
				e.printStackTrace();
			}
		};
	}
}
