package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessUser;
import com.epiis.finalproject.dto.request.user.RequestUserInsert;
import com.epiis.finalproject.dto.request.user.RequestUserUpdate;
import com.epiis.finalproject.dto.response.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class UserControllerTest extends BaseControllerTest {

    private BusinessUser businessUser;
    private UserController controller;

    @BeforeEach
    void setUp() {
        businessUser = mock(BusinessUser.class);
        controller = new UserController(businessUser);
    }

    @Test
    void testInsert() {
        RequestUserInsert req = new RequestUserInsert();
        ResponseUserInsert resp = new ResponseUserInsert();
        resp.success();
        when(businessUser.insert(req)).thenReturn(resp);
        assertOk(controller.insert(req));
    }

    @Test
    void testGetAll() {
        when(businessUser.getAll()).thenReturn(emptyMap());
        assertOk(controller.getAll());
    }

    @Test
    void testGetById() {
        when(businessUser.getById("u1")).thenReturn(emptyMap());
        assertOk(controller.getById("u1"));
    }

    @Test
    void testDeleteById() {
        when(businessUser.deleteById("u1")).thenReturn(new ResponseUserDeleteById());
        assertOk(controller.deleteById("u1"));
    }

    @Test
    void testUpdate() {
        RequestUserUpdate req = new RequestUserUpdate();
        when(businessUser.update("u1", req)).thenReturn(new ResponseUserUpdate());
        assertOk(controller.update("u1", req));
    }
}
