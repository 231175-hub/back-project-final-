package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.professor.RequestProfessorInsert;
import com.epiis.finalproject.dto.request.professor.RequestProfessorUpdate;
import com.epiis.finalproject.dto.response.professor.*;
import com.epiis.finalproject.entity.EntityProfessor;
import com.epiis.finalproject.repository.RepositoryProfessor;
import com.epiis.finalproject.repository.RepositoryRole;
import com.epiis.finalproject.repository.RepositoryUser;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
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
    void testGetAllAndGetById() {
        when(repositoryProfessor.findAll()).thenReturn(Collections.emptyList());
        when(repositoryProfessor.findById("prof1")).thenReturn(Optional.empty());

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
    void testUpdateProfessorNotFound() {
        when(repositoryProfessor.findById("prof1")).thenReturn(Optional.empty());

        RequestProfessorUpdate updateReq = new RequestProfessorUpdate();
        ResponseProfessorUpdate response = businessProfessor.update("prof1", updateReq);
        assertEquals("error", response.getType());
    }
}
