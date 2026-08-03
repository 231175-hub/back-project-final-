package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessStudent;
import com.epiis.finalproject.dto.request.student.RequestStudentInsert;
import com.epiis.finalproject.dto.request.student.RequestStudentUpdate;
import com.epiis.finalproject.dto.response.student.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class StudentControllerTest extends BaseControllerTest {

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
        assertOk(controller.insert(req));
    }

    @Test
    void testGetAll() {
        when(businessStudent.getAll()).thenReturn(emptyMap());
        assertOk(controller.getAll());
    }

    @Test
    void testGetById() {
        when(businessStudent.getById("s1")).thenReturn(emptyMap());
        assertOk(controller.getById("s1"));
    }

    @Test
    void testDeleteById() {
        when(businessStudent.deleteById("s1")).thenReturn(new ResponseStudentDeleteById());
        assertOk(controller.deleteById("s1"));
    }

    @Test
    void testUpdate() {
        RequestStudentUpdate req = new RequestStudentUpdate();
        when(businessStudent.update("s1", req)).thenReturn(new ResponseStudentUpdate());
        assertOk(controller.update("s1", req));
    }
}
