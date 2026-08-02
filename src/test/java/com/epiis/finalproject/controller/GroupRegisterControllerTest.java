package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessGroupRegister;
import com.epiis.finalproject.dto.request.groupregister.RequestGroupRegisterSave;
import com.epiis.finalproject.dto.response.groupregister.ResponseGroupRegisterData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupRegisterControllerTest {

    private BusinessGroupRegister businessGroupRegister;
    private GroupRegisterController controller;

    @BeforeEach
    void setUp() {
        businessGroupRegister = mock(BusinessGroupRegister.class);
        controller = new GroupRegisterController(businessGroupRegister);
    }

    @Test
    void testGetGroupRegisterData() {
        ResponseGroupRegisterData resp = new ResponseGroupRegisterData();
        resp.setSuccess(true);
        when(businessGroupRegister.getGroupRegisterData("g1")).thenReturn(resp);

        ResponseEntity<ResponseGroupRegisterData> result = controller.getGroupRegisterData("g1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testSaveGroupRegisterData() {
        RequestGroupRegisterSave req = new RequestGroupRegisterSave();
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        when(businessGroupRegister.saveGroupRegisterData("g1", req)).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.saveGroupRegisterData("g1", req);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testCloseGroupRegister() {
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        when(businessGroupRegister.closeGroupRegister("g1")).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.closeGroupRegister("g1");
        assertEquals(200, result.getStatusCode().value());
    }
}
