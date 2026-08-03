package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessSchool;
import com.epiis.finalproject.dto.request.school.RequestSchoolInsert;
import com.epiis.finalproject.dto.response.school.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class SchoolControllerTest extends BaseControllerTest {

    private BusinessSchool businessSchool;
    private SchoolController schoolController;

    @BeforeEach
    void setUp() {
        businessSchool = mock(BusinessSchool.class);
        schoolController = new SchoolController(businessSchool);
    }

    @Test
    void testActionInsert() throws Exception {
        RequestSchoolInsert req = new RequestSchoolInsert();
        ResponseSchoolInsert resp = new ResponseSchoolInsert();
        resp.success();
        when(businessSchool.insert(req)).thenReturn(resp);
        assertOk(schoolController.actionInsert(req));
    }

    @Test
    void testGetAll() {
        when(businessSchool.getAll()).thenReturn(emptyMap());
        assertOk(schoolController.getAll());
    }

    @Test
    void testGetById() {
        when(businessSchool.getById("s1")).thenReturn(emptyMap());
        assertOk(schoolController.getById("s1"));
    }

    @Test
    void testDeleteById() throws Exception {
        when(businessSchool.deleteById("s1")).thenReturn(new ResponseSchoolDeleteById());
        assertOk(schoolController.deleteById("s1"));
    }
}
