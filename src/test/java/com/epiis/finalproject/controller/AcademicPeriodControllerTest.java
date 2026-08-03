package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessAcademicPeriod;
import com.epiis.finalproject.dto.request.academicperiod.RequestAcademicPeriodInsert;
import com.epiis.finalproject.dto.request.academicperiod.RequestAcademicPeriodUpdate;
import com.epiis.finalproject.dto.response.academicperiod.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class AcademicPeriodControllerTest extends BaseControllerTest {

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
        assertOk(controller.insert(req));
    }

    @Test
    void testGetAll() {
        when(businessAcademicPeriod.getAll()).thenReturn(emptyMap());
        assertOk(controller.getAll());
    }

    @Test
    void testGetById() {
        when(businessAcademicPeriod.getById("p1")).thenReturn(emptyMap());
        assertOk(controller.getById("p1"));
    }

    @Test
    void testDeleteById() {
        ResponseAcademicPeriodDeleteById resp = new ResponseAcademicPeriodDeleteById();
        when(businessAcademicPeriod.deleteById("p1")).thenReturn(resp);
        assertOk(controller.deleteById("p1"));
    }

    @Test
    void testGetStatuses() {
        assertOk(controller.getStatuses());
    }

    @Test
    void testUpdate() {
        RequestAcademicPeriodUpdate req = new RequestAcademicPeriodUpdate();
        ResponseAcademicPeriodUpdate resp = new ResponseAcademicPeriodUpdate();
        resp.setType("success");
        when(businessAcademicPeriod.update("p1", req)).thenReturn(resp);
        assertOk(controller.update("p1", req));
    }
}
