package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.role.RequestRoleInsert;
import com.epiis.finalproject.dto.request.role.RequestRoleUpdate;
import com.epiis.finalproject.dto.response.role.ResponseRoleDeleteById;
import com.epiis.finalproject.dto.response.role.ResponseRoleInsert;
import com.epiis.finalproject.dto.response.role.ResponseRoleUpdate;
import com.epiis.finalproject.entity.EntityRole;
import com.epiis.finalproject.repository.RepositoryRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BusinessRoleTest {

    private RepositoryRole repositoryRole;
    private BusinessRole businessRole;

    @BeforeEach
    void setUp() {
        repositoryRole = mock(RepositoryRole.class);
        businessRole = new BusinessRole(repositoryRole);
    }

    @Test
    void testInsertRole() {
        RequestRoleInsert request = new RequestRoleInsert();
        request.setNameRole("ROLE_ADMIN");

        ResponseRoleInsert response = businessRole.insert(request);

        assertEquals("success", response.getType());
        verify(repositoryRole, times(1)).save(any(EntityRole.class));
    }

    @Test
    void testGetAllRoles() {
        EntityRole role1 = new EntityRole();
        role1.setIdRole("r1");
        role1.setNameRole("ADMIN");

        when(repositoryRole.findAll()).thenReturn(List.of(role1));

        Map<String, Object> result = businessRole.getAll();
        assertNotNull(result);
        assertTrue(result.containsKey("data"));
        assertEquals(1, ((List<?>) result.get("data")).size());
    }

    @Test
    void testGetByIdRole() {
        EntityRole role1 = new EntityRole();
        role1.setIdRole("r1");
        role1.setNameRole("ADMIN");

        when(repositoryRole.findById("r1")).thenReturn(Optional.of(role1));

        Map<String, Object> result = businessRole.getById("r1");
        assertNotNull(result);
        assertTrue(result.containsKey("data"));
    }

    @Test
    void testDeleteByIdRole() {
        ResponseRoleDeleteById response = businessRole.deleteById("r1");
        assertEquals("success", response.getType());
        verify(repositoryRole, times(1)).deleteById("r1");
    }

    @Test
    void testUpdateRoleSuccess() {
        EntityRole role1 = new EntityRole();
        role1.setIdRole("r1");
        role1.setNameRole("ADMIN");

        when(repositoryRole.findById("r1")).thenReturn(Optional.of(role1));

        RequestRoleUpdate updateReq = new RequestRoleUpdate();
        updateReq.setNameRole("SUPER_ADMIN");

        ResponseRoleUpdate response = businessRole.update("r1", updateReq);
        assertEquals("success", response.getType());
        verify(repositoryRole, times(1)).save(any(EntityRole.class));
    }

    @Test
    void testUpdateRoleNotFound() {
        when(repositoryRole.findById("nonexistent")).thenReturn(Optional.empty());

        RequestRoleUpdate updateReq = new RequestRoleUpdate();
        updateReq.setNameRole("SUPER_ADMIN");

        ResponseRoleUpdate response = businessRole.update("nonexistent", updateReq);
        assertEquals("error", response.getType());
        verify(repositoryRole, never()).save(any(EntityRole.class));
    }
}
