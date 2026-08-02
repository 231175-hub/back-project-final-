package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessStudent;
import com.epiis.finalproject.dto.request.student.RequestStudentInsert;
import com.epiis.finalproject.dto.request.student.RequestStudentUpdate;
import com.epiis.finalproject.dto.response.student.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentControllerTest {

    private BusinessStudent businessStudent;
    private StudentController controller;

    @BeforeEach
    void setUp() {
        businessStudent = mock(BusinessStudent.class);
        controller = new StudentController(businessStudent);
    }

    @Test
    void testInsert() {
        RequestStudentInsert req = new RequestStudentInsert();
        ResponseStudentInsert resp = new ResponseStudentInsert();
        resp.success();
        when(businessStudent.insert(req)).thenReturn(resp);

        ResponseEntity<ResponseStudentInsert> result = controller.insert(req);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetAll() {
        Map<String, Object> map = new HashMap<>();
        when(businessStudent.getAll()).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.getAll();
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetById() {
        Map<String, Object> map = new HashMap<>();
        when(businessStudent.getById("s1")).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.getById("s1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testDeleteById() {
        ResponseStudentDeleteById resp = new ResponseStudentDeleteById();
        when(businessStudent.deleteById("s1")).thenReturn(resp);

        ResponseEntity<ResponseStudentDeleteById> result = controller.deleteById("s1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testUpdate() {
        RequestStudentUpdate req = new RequestStudentUpdate();
        ResponseStudentUpdate resp = new ResponseStudentUpdate();
        when(businessStudent.update("s1", req)).thenReturn(resp);

        ResponseEntity<ResponseStudentUpdate> result = controller.update("s1", req);
        assertEquals(200, result.getStatusCode().value());
    }
}
