package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessProfessor;
import com.epiis.finalproject.dto.request.professor.RequestProfessorInsert;
import com.epiis.finalproject.dto.request.professor.RequestProfessorUpdate;
import com.epiis.finalproject.dto.response.professor.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfessorControllerTest {

    private BusinessProfessor businessProfessor;
    private ProfessorController controller;

    @BeforeEach
    void setUp() {
        businessProfessor = mock(BusinessProfessor.class);
        controller = new ProfessorController(businessProfessor);
    }

    @Test
    void testInsert() {
        RequestProfessorInsert req = new RequestProfessorInsert();
        ResponseProfessorInsert resp = new ResponseProfessorInsert();
        resp.success();
        when(businessProfessor.insert(req)).thenReturn(resp);

        ResponseEntity<ResponseProfessorInsert> result = controller.insert(req);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetAll() {
        Map<String, Object> map = new HashMap<>();
        when(businessProfessor.getAll()).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.getAll();
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetById() {
        Map<String, Object> map = new HashMap<>();
        when(businessProfessor.getById("p1")).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.getById("p1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testDeleteById() {
        ResponseProfessorDeleteById resp = new ResponseProfessorDeleteById();
        when(businessProfessor.deleteById("p1")).thenReturn(resp);

        ResponseEntity<ResponseProfessorDeleteById> result = controller.deleteById("p1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testUpdate() {
        RequestProfessorUpdate req = new RequestProfessorUpdate();
        ResponseProfessorUpdate resp = new ResponseProfessorUpdate();
        when(businessProfessor.update("p1", req)).thenReturn(resp);

        ResponseEntity<ResponseProfessorUpdate> result = controller.update("p1", req);
        assertEquals(200, result.getStatusCode().value());
    }
}
