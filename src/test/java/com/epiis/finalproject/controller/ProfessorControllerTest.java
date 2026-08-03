package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessProfessor;
import com.epiis.finalproject.dto.request.professor.RequestProfessorInsert;
import com.epiis.finalproject.dto.request.professor.RequestProfessorUpdate;
import com.epiis.finalproject.dto.response.professor.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ProfessorControllerTest extends BaseControllerTest {

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
        assertOk(controller.insert(req));
    }

    @Test
    void testGetAll() {
        when(businessProfessor.getAll()).thenReturn(emptyMap());
        assertOk(controller.getAll());
    }

    @Test
    void testGetById() {
        when(businessProfessor.getById("p1")).thenReturn(emptyMap());
        assertOk(controller.getById("p1"));
    }

    @Test
    void testDeleteById() {
        when(businessProfessor.deleteById("p1")).thenReturn(new ResponseProfessorDeleteById());
        assertOk(controller.deleteById("p1"));
    }

    @Test
    void testUpdate() {
        RequestProfessorUpdate req = new RequestProfessorUpdate();
        when(businessProfessor.update("p1", req)).thenReturn(new ResponseProfessorUpdate());
        assertOk(controller.update("p1", req));
    }
}
