package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessRole;
import com.epiis.finalproject.dto.request.role.RequestRoleInsert;
import com.epiis.finalproject.dto.request.role.RequestRoleUpdate;
import com.epiis.finalproject.dto.response.role.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleControllerTest {

    private BusinessRole businessRole;
    private RoleController controller;

    @BeforeEach
    void setUp() {
        businessRole = mock(BusinessRole.class);
        controller = new RoleController(businessRole);
    }

    @Test
    void testInsert() {
        RequestRoleInsert req = new RequestRoleInsert();
        ResponseRoleInsert resp = new ResponseRoleInsert();
        resp.success();
        when(businessRole.insert(req)).thenReturn(resp);

        ResponseEntity<ResponseRoleInsert> result = controller.insert(req);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetAll() {
        Map<String, Object> map = new HashMap<>();
        when(businessRole.getAll()).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.getAll();
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetById() {
        Map<String, Object> map = new HashMap<>();
        when(businessRole.getById("r1")).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.getById("r1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testDeleteById() {
        ResponseRoleDeleteById resp = new ResponseRoleDeleteById();
        when(businessRole.deleteById("r1")).thenReturn(resp);

        ResponseEntity<ResponseRoleDeleteById> result = controller.deleteById("r1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testUpdate() {
        RequestRoleUpdate req = new RequestRoleUpdate();
        ResponseRoleUpdate resp = new ResponseRoleUpdate();
        when(businessRole.update("r1", req)).thenReturn(resp);

        ResponseEntity<ResponseRoleUpdate> result = controller.update("r1", req);
        assertEquals(200, result.getStatusCode().value());
    }
}
