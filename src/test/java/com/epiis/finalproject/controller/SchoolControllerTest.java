package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessSchool;
import com.epiis.finalproject.dto.request.school.RequestSchoolInsert;
import com.epiis.finalproject.dto.response.school.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SchoolControllerTest {

    private BusinessSchool businessSchool;
    private SchoolController schoolController;

    @BeforeEach
    void setUp() {
        businessSchool = mock(BusinessSchool.class);
        schoolController = new SchoolController(businessSchool);
    }

    @Test
    void testActionInsert() throws IOException {
        RequestSchoolInsert req = new RequestSchoolInsert();
        ResponseSchoolInsert resp = new ResponseSchoolInsert();
        resp.success();
        when(businessSchool.insert(req)).thenReturn(resp);

        ResponseEntity<ResponseSchoolInsert> result = schoolController.actionInsert(req);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetAll() {
        Map<String, Object> map = new HashMap<>();
        map.put("data", Collections.emptyList());
        when(businessSchool.getAll()).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = schoolController.getAll();
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetById() {
        Map<String, Object> map = new HashMap<>();
        when(businessSchool.getById("s1")).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = schoolController.getById("s1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testDeleteById() throws IOException {
        ResponseSchoolDeleteById resp = new ResponseSchoolDeleteById();
        when(businessSchool.deleteById("s1")).thenReturn(resp);

        ResponseEntity<ResponseSchoolDeleteById> result = schoolController.deleteById("s1");
        assertEquals(200, result.getStatusCode().value());
    }
}
