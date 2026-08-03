package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessUnitScore;
import com.epiis.finalproject.dto.request.unitscore.RequestUnitScoreInsert;
import com.epiis.finalproject.dto.request.unitscore.RequestUnitScoreUpdate;
import com.epiis.finalproject.dto.response.unitscore.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class UnitScoreControllerTest extends BaseControllerTest {

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
        assertOk(controller.insert(req));
    }

    @Test
    void testGetAll() {
        when(businessUnitScore.getAll()).thenReturn(emptyMap());
        assertOk(controller.getAll());
    }

    @Test
    void testGetById() {
        when(businessUnitScore.getById("us1")).thenReturn(emptyMap());
        assertOk(controller.getById("us1"));
    }

    @Test
    void testDeleteById() {
        when(businessUnitScore.deleteById("us1")).thenReturn(new ResponseUnitScoreDeleteById());
        assertOk(controller.deleteById("us1"));
    }

    @Test
    void testUpdate() {
        RequestUnitScoreUpdate req = new RequestUnitScoreUpdate();
        when(businessUnitScore.update("us1", req)).thenReturn(new ResponseUnitScoreUpdate());
        assertOk(controller.update("us1", req));
    }
}
