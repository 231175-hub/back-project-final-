package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessSchedule;
import com.epiis.finalproject.dto.request.schedule.RequestScheduleInsert;
import com.epiis.finalproject.dto.request.schedule.RequestScheduleUpdate;
import com.epiis.finalproject.dto.response.schedule.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduleControllerTest {

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

        ResponseEntity<ResponseScheduleInsert> result = controller.insert(List.of(req));
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetAll() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user@email.com");
        when(businessSchedule.getStudentScheduleByUserEmail("user@email.com")).thenReturn(Collections.emptyList());

        ResponseEntity<List<ResponseScheduleGetAll>> result = controller.getAll(principal);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetById() {
        Map<String, Object> map = new HashMap<>();
        when(businessSchedule.getById("s1")).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.getById("s1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testDeleteById() {
        ResponseScheduleDeleteById resp = new ResponseScheduleDeleteById();
        when(businessSchedule.deleteById("s1")).thenReturn(resp);

        ResponseEntity<ResponseScheduleDeleteById> result = controller.deleteById("s1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testUpdate() {
        RequestScheduleUpdate req = new RequestScheduleUpdate();
        ResponseScheduleUpdate resp = new ResponseScheduleUpdate();
        when(businessSchedule.update("s1", req)).thenReturn(resp);

        ResponseEntity<ResponseScheduleUpdate> result = controller.update("s1", req);
        assertEquals(200, result.getStatusCode().value());
    }
}
