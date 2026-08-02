package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessUser;
import com.epiis.finalproject.dto.request.user.RequestUserInsert;
import com.epiis.finalproject.dto.request.user.RequestUserUpdate;
import com.epiis.finalproject.dto.response.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

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

        ResponseEntity<ResponseUserInsert> result = controller.insert(req);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetAll() {
        Map<String, Object> map = new HashMap<>();
        when(businessUser.getAll()).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.getAll();
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetById() {
        Map<String, Object> map = new HashMap<>();
        when(businessUser.getById("u1")).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.getById("u1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testDeleteById() {
        ResponseUserDeleteById resp = new ResponseUserDeleteById();
        when(businessUser.deleteById("u1")).thenReturn(resp);

        ResponseEntity<ResponseUserDeleteById> result = controller.deleteById("u1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testUpdate() {
        RequestUserUpdate req = new RequestUserUpdate();
        ResponseUserUpdate resp = new ResponseUserUpdate();
        when(businessUser.update("u1", req)).thenReturn(resp);

        ResponseEntity<ResponseUserUpdate> result = controller.update("u1", req);
        assertEquals(200, result.getStatusCode().value());
    }
}
