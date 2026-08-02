package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessGroupStudent;
import com.epiis.finalproject.dto.request.groupstudent.RequestGroupStundentInsert;
import com.epiis.finalproject.dto.request.groupstudent.RequestGroupStudentUpdate;
import com.epiis.finalproject.dto.response.group.*;
import com.epiis.finalproject.dto.response.groupstudent.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupStudentControllerTest {

    private BusinessGroupStudent businessGroupStudent;
    private GroupStudentController controller;

    @BeforeEach
    void setUp() {
        businessGroupStudent = mock(BusinessGroupStudent.class);
        controller = new GroupStudentController(businessGroupStudent);
    }

    @Test
    void testInsert() {
        RequestGroupStundentInsert req = new RequestGroupStundentInsert();
        ResponseGroupStudentInsert resp = new ResponseGroupStudentInsert();
        resp.success();
        when(businessGroupStudent.insert(req)).thenReturn(resp);

        ResponseEntity<ResponseGroupStudentInsert> result = controller.insert(req);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetAll() {
        Map<String, Object> map = new HashMap<>();
        when(businessGroupStudent.getAll()).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.insert();
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetById() {
        Map<String, Object> map = new HashMap<>();
        when(businessGroupStudent.getById("gs1")).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.getById("gs1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testDeleteById() {
        ResponseGroupDeleteById resp = new ResponseGroupDeleteById();
        when(businessGroupStudent.deleteById("gs1")).thenReturn(resp);

        ResponseEntity<ResponseGroupDeleteById> result = controller.deleteById("gs1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testUpdate() {
        RequestGroupStudentUpdate req = new RequestGroupStudentUpdate();
        ResponseGroupStudentUpdate resp = new ResponseGroupStudentUpdate();
        when(businessGroupStudent.update("gs1", req)).thenReturn(resp);

        ResponseEntity<ResponseGroupStudentUpdate> result = controller.update("gs1", req);
        assertEquals(200, result.getStatusCode().value());
    }
}
