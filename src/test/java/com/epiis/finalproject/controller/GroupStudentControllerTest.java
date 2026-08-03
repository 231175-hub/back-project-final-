package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessGroupStudent;
import com.epiis.finalproject.dto.request.groupstudent.RequestGroupStundentInsert;
import com.epiis.finalproject.dto.request.groupstudent.RequestGroupStudentUpdate;
import com.epiis.finalproject.dto.response.group.ResponseGroupDeleteById;
import com.epiis.finalproject.dto.response.groupstudent.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class GroupStudentControllerTest extends BaseControllerTest {

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
        assertOk(controller.insert(req));
    }

    @Test
    void testGetAll() {
        when(businessGroupStudent.getAll()).thenReturn(emptyMap());
        assertOk(controller.insert());
    }

    @Test
    void testGetById() {
        when(businessGroupStudent.getById("gs1")).thenReturn(emptyMap());
        assertOk(controller.getById("gs1"));
    }

    @Test
    void testDeleteById() {
        when(businessGroupStudent.deleteById("gs1")).thenReturn(new ResponseGroupDeleteById());
        assertOk(controller.deleteById("gs1"));
    }

    @Test
    void testUpdate() {
        RequestGroupStudentUpdate req = new RequestGroupStudentUpdate();
        when(businessGroupStudent.update("gs1", req)).thenReturn(new ResponseGroupStudentUpdate());
        assertOk(controller.update("gs1", req));
    }
}
