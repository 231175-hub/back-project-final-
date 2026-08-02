package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessUnits;
import com.epiis.finalproject.dto.request.units.RequestUnitsInsert;
import com.epiis.finalproject.dto.request.units.RequestUnitsUpdate;
import com.epiis.finalproject.dto.response.units.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UnitsControllerTest {

    private BusinessUnits businessUnits;
    private UnitsController controller;

    @BeforeEach
    void setUp() {
        businessUnits = mock(BusinessUnits.class);
        controller = new UnitsController(businessUnits);
    }

    @Test
    void testInsert() {
        RequestUnitsInsert req = new RequestUnitsInsert();
        ResponseUnitsInsert resp = new ResponseUnitsInsert();
        resp.success();
        when(businessUnits.insert(req)).thenReturn(resp);

        ResponseEntity<ResponseUnitsInsert> result = controller.insert(req);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetAll() {
        Map<String, Object> map = new HashMap<>();
        when(businessUnits.getAll()).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.getAll();
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetById() {
        Map<String, Object> map = new HashMap<>();
        when(businessUnits.getById("u1")).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.getById("u1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testDeleteById() {
        ResponseUnitsDeleteById resp = new ResponseUnitsDeleteById();
        when(businessUnits.deleteById("u1")).thenReturn(resp);

        ResponseEntity<ResponseUnitsDeleteById> result = controller.deleteById("u1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testUpdate() {
        RequestUnitsUpdate req = new RequestUnitsUpdate();
        ResponseUnitsUpdate resp = new ResponseUnitsUpdate();
        when(businessUnits.update("u1", req)).thenReturn(resp);

        ResponseEntity<ResponseUnitsUpdate> result = controller.insert("u1", req);
        assertEquals(200, result.getStatusCode().value());
    }
}
