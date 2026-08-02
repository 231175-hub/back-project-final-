package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.professor.RequestProfessorInsert;
import com.epiis.finalproject.dto.request.professor.RequestProfessorUpdate;
import com.epiis.finalproject.dto.request.user.RequestUserUpdatePassword;
import com.epiis.finalproject.dto.response.professor.ResponseProfessorDeleteById;
import com.epiis.finalproject.dto.response.professor.ResponseProfessorInsert;
import com.epiis.finalproject.dto.response.professor.ResponseProfessorUpdate;
import com.epiis.finalproject.dto.response.user.ResponseUserUpdatePassword;
import com.epiis.finalproject.entity.EntityRole;
import com.epiis.finalproject.entity.EntityUser;
import com.epiis.finalproject.repository.RepositoryProfessor;
import com.epiis.finalproject.repository.RepositoryRole;
import com.epiis.finalproject.repository.RepositoryUser;
import com.epiis.finalproject.staticdata.EnumRoles;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BusinessProfessorTest {

    private RepositoryProfessor repositoryProfessor;
    private RepositoryUser repositoryUser;
    private PasswordEncoder passwordEncoder;
    private RepositoryRole repositoryRole;
    private EntityManager entityManager;
    private BusinessProfessor businessProfessor;

    @BeforeEach
    void setUp() {
        repositoryProfessor = mock(RepositoryProfessor.class);
        repositoryUser = mock(RepositoryUser.class);
        passwordEncoder = mock(PasswordEncoder.class);
        repositoryRole = mock(RepositoryRole.class);
        entityManager = mock(EntityManager.class);

        businessProfessor = new BusinessProfessor(
                repositoryProfessor, repositoryUser, passwordEncoder, repositoryRole, entityManager
        );
    }

    @Test
    void testInsertRoleNotFound() {
        when(repositoryRole.findByNameRole(EnumRoles.PROFESSOR.toString())).thenReturn(Optional.empty());

        RequestProfessorInsert request = new RequestProfessorInsert();
        ResponseProfessorInsert response = businessProfessor.insert(request);

        assertEquals("error", response.getType());
        assertTrue(response.getListMessage().get(0).contains("El rol PROFESSOR no está configurado"));
    }

    @Test
    void testInsertEmailAlreadyExists() {
        EntityRole role = new EntityRole();
        when(repositoryRole.findByNameRole(EnumRoles.PROFESSOR.toString())).thenReturn(Optional.of(role));

        RequestProfessorInsert request = new RequestProfessorInsert();
        request.setEmail("prof@test.com");
        when(repositoryUser.findByEmail("prof@test.com")).thenReturn(Optional.of(new EntityUser()));

        ResponseProfessorInsert response = businessProfessor.insert(request);

        assertEquals("error", response.getType());
        assertTrue(response.getListMessage().get(0).contains("correo electrónico ya está registrado"));
    }

    @Test
    void testInsertSuccess() {
        EntityRole role = new EntityRole();
        when(repositoryRole.findByNameRole(EnumRoles.PROFESSOR.toString())).thenReturn(Optional.of(role));
        when(repositoryUser.findByEmail("prof@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        RequestProfessorInsert request = new RequestProfessorInsert();
        request.setFirstName("Juan");
        request.setSurName("Perez");
        request.setEmail("prof@test.com");
        request.setPassword("123456");

        ResponseProfessorInsert response = businessProfessor.insert(request);

        assertEquals("success", response.getType());
        verify(entityManager, times(1)).persist(any(EntityUser.class));
    }

    @Test
    void testGetAllAndGetById() {
        when(repositoryProfessor.findAll()).thenReturn(Collections.emptyList());
        when(repositoryUser.findById("prof1")).thenReturn(Optional.empty());

        Map<String, Object> all = businessProfessor.getAll();
        assertNotNull(all.get("data"));

        Map<String, Object> byId = businessProfessor.getById("prof1");
        assertNotNull(byId.get("data"));
    }

    @Test
    void testDeleteById() {
        ResponseProfessorDeleteById response = businessProfessor.deleteById("prof1");
        assertEquals("success", response.getType());
        verify(repositoryProfessor, times(1)).deleteById("prof1");
    }

    @Test
    void testUpdateNotFound() {
        when(repositoryUser.findById("prof1")).thenReturn(Optional.empty());

        RequestProfessorUpdate request = new RequestProfessorUpdate();
        ResponseProfessorUpdate res = businessProfessor.update("prof1", request);
        assertEquals("error", res.getType());
    }

    @Test
    void testUpdateSuccess() {
        EntityUser user = new EntityUser();
        when(repositoryUser.findById("prof1")).thenReturn(Optional.of(user));

        RequestProfessorUpdate request = new RequestProfessorUpdate();
        request.setFirstName("Nuevo");
        request.setSurName("Nombre");
        request.setEmail("nuevo@test.com");

        ResponseProfessorUpdate res = businessProfessor.update("prof1", request);
        assertEquals("success", res.getType());
        verify(repositoryUser, times(1)).save(user);
    }

    @Test
    void testUpdatePassword() {
        when(repositoryUser.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        RequestUserUpdatePassword request = new RequestUserUpdatePassword();
        request.setPassword("newpass");

        ResponseUserUpdatePassword res1 = businessProfessor.updatePassword("nonexistent@test.com", request);
        assertEquals("error", res1.getType());

        EntityUser user = new EntityUser();
        when(repositoryUser.findByEmail("prof@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass")).thenReturn("encodedNew");

        ResponseUserUpdatePassword res2 = businessProfessor.updatePassword("prof@test.com", request);
        assertEquals("success", res2.getType());
        verify(repositoryUser, times(1)).save(user);
    }

    @Test
    void testSearchProfessorsForAutocomplete() {
        assertTrue(businessProfessor.searchProfessorsForAutocomplete(null).isEmpty());
        assertTrue(businessProfessor.searchProfessorsForAutocomplete("   ").isEmpty());

        EntityUser prof = new EntityUser();
        prof.setIdUser("u1");
        prof.setFirstName("Carlos");
        prof.setSurName("Gomez");

        when(repositoryUser.searchProfessorsByTerm(eq("Gomez"), any())).thenReturn(List.of(prof));

        List<Map<String, Object>> result = businessProfessor.searchProfessorsForAutocomplete("Gomez");
        assertEquals(1, result.size());
        assertEquals("Gomez Carlos", result.get(0).get("fullName"));
    }
}
