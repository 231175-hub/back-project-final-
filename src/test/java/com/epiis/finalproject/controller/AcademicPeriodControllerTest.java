package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessAcademicPeriod;
import com.epiis.finalproject.dto.request.academicperiod.RequestAcademicPeriodInsert;
import com.epiis.finalproject.dto.request.academicperiod.RequestAcademicPeriodUpdate;
import com.epiis.finalproject.dto.response.academicperiod.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AcademicPeriodControllerTest {

    private BusinessAcademicPeriod businessAcademicPeriod;
    private AcademicPeriodController controller;

    @BeforeEach
    void setUp() {
        businessAcademicPeriod = mock(BusinessAcademicPeriod.class);
        controller = new AcademicPeriodController(businessAcademicPeriod);
    }

    @Test
    void testInsert() {
        RequestAcademicPeriodInsert req = new RequestAcademicPeriodInsert();
        ResponseAcademicPeriodInsert resp = new ResponseAcademicPeriodInsert();
        resp.success();
        when(businessAcademicPeriod.insert(req)).thenReturn(resp);

        ResponseEntity<ResponseAcademicPeriodInsert> result = controller.insert(req);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetAll() {
        Map<String, Object> map = new HashMap<>();
        when(businessAcademicPeriod.getAll()).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.getAll();
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetById() {
        Map<String, Object> map = new HashMap<>();
        when(businessAcademicPeriod.getById("p1")).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.getById("p1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testDeleteById() {
        ResponseAcademicPeriodDeleteById resp = new ResponseAcademicPeriodDeleteById();
        when(businessAcademicPeriod.deleteById("p1")).thenReturn(resp);

        ResponseEntity<ResponseAcademicPeriodDeleteById> result = controller.deleteById("p1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetStatuses() {
        ResponseEntity<Object> result = controller.getStatuses();
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testUpdate() {
        RequestAcademicPeriodUpdate req = new RequestAcademicPeriodUpdate();
        ResponseAcademicPeriodUpdate resp = new ResponseAcademicPeriodUpdate();
        resp.setType("success");
        when(businessAcademicPeriod.update("p1", req)).thenReturn(resp);

        ResponseEntity<ResponseAcademicPeriodUpdate> result = controller.update("p1", req);
        assertEquals(200, result.getStatusCode().value());
    }
}
