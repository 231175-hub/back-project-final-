package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessRole;
import com.epiis.finalproject.dto.request.role.RequestRoleInsert;
import com.epiis.finalproject.dto.request.role.RequestRoleUpdate;
import com.epiis.finalproject.dto.response.role.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class RoleControllerTest extends BaseControllerTest {

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
        assertOk(controller.insert(req));
    }

    @Test
    void testGetAll() {
        when(businessRole.getAll()).thenReturn(emptyMap());
        assertOk(controller.getAll());
    }

    @Test
    void testGetById() {
        when(businessRole.getById("r1")).thenReturn(emptyMap());
        assertOk(controller.getById("r1"));
    }

    @Test
    void testDeleteById() {
        when(businessRole.deleteById("r1")).thenReturn(new ResponseRoleDeleteById());
        assertOk(controller.deleteById("r1"));
    }

    @Test
    void testUpdate() {
        RequestRoleUpdate req = new RequestRoleUpdate();
        when(businessRole.update("r1", req)).thenReturn(new ResponseRoleUpdate());
        assertOk(controller.update("r1", req));
    }
}
