package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessSchedule;
import com.epiis.finalproject.dto.request.schedule.RequestScheduleInsert;
import com.epiis.finalproject.dto.request.schedule.RequestScheduleUpdate;
import com.epiis.finalproject.dto.response.schedule.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class ScheduleControllerTest extends BaseControllerTest {

    private BusinessSchedule businessSchedule;
    private ScheduleController controller;

    @BeforeEach
    void setUp() {
        businessSchedule = mock(BusinessSchedule.class);
        controller = new ScheduleController(businessSchedule);
    }

    @Test
    void testInsert() {
        RequestScheduleInsert req = new RequestScheduleInsert();
        ResponseScheduleInsert resp = new ResponseScheduleInsert();
        resp.success();
        when(businessSchedule.insert(List.of(req))).thenReturn(resp);
        assertOk(controller.insert(List.of(req)));
    }

    @Test
    void testGetAll() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user@email.com");
        when(businessSchedule.getStudentScheduleByUserEmail("user@email.com")).thenReturn(Collections.emptyList());
        assertOk(controller.getAll(principal));
    }

    @Test
    void testGetById() {
        when(businessSchedule.getById("s1")).thenReturn(emptyMap());
        assertOk(controller.getById("s1"));
    }

    @Test
    void testDeleteById() {
        when(businessSchedule.deleteById("s1")).thenReturn(new ResponseScheduleDeleteById());
        assertOk(controller.deleteById("s1"));
    }

    @Test
    void testUpdate() {
        RequestScheduleUpdate req = new RequestScheduleUpdate();
        when(businessSchedule.update("s1", req)).thenReturn(new ResponseScheduleUpdate());
        assertOk(controller.update("s1", req));
    }
}
