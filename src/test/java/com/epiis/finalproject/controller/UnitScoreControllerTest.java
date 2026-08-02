package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessUnitScore;
import com.epiis.finalproject.dto.request.unitscore.RequestUnitScoreInsert;
import com.epiis.finalproject.dto.request.unitscore.RequestUnitScoreUpdate;
import com.epiis.finalproject.dto.response.unitscore.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UnitScoreControllerTest {

    private BusinessUnitScore businessUnitScore;
    private UnitScoreController controller;

    @BeforeEach
    void setUp() {
        businessUnitScore = mock(BusinessUnitScore.class);
        controller = new UnitScoreController(businessUnitScore);
    }

    @Test
    void testInsert() {
        RequestUnitScoreInsert req = new RequestUnitScoreInsert();
        ResponseUnitScoreInsert resp = new ResponseUnitScoreInsert();
        resp.success();
        when(businessUnitScore.insert(req)).thenReturn(resp);

        ResponseEntity<ResponseUnitScoreInsert> result = controller.insert(req);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetAll() {
        Map<String, Object> map = new HashMap<>();
        when(businessUnitScore.getAll()).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.getAll();
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetById() {
        Map<String, Object> map = new HashMap<>();
        when(businessUnitScore.getById("us1")).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = controller.getById("us1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testDeleteById() {
        ResponseUnitScoreDeleteById resp = new ResponseUnitScoreDeleteById();
        when(businessUnitScore.deleteById("us1")).thenReturn(resp);

        ResponseEntity<ResponseUnitScoreDeleteById> result = controller.deleteById("us1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testUpdate() {
        RequestUnitScoreUpdate req = new RequestUnitScoreUpdate();
        ResponseUnitScoreUpdate resp = new ResponseUnitScoreUpdate();
        when(businessUnitScore.update("us1", req)).thenReturn(resp);

        ResponseEntity<ResponseUnitScoreUpdate> result = controller.update("us1", req);
        assertEquals(200, result.getStatusCode().value());
    }
}
