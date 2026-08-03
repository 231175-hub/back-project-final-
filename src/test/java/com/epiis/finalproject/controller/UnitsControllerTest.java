package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessUnits;
import com.epiis.finalproject.dto.request.units.RequestUnitsInsert;
import com.epiis.finalproject.dto.request.units.RequestUnitsUpdate;
import com.epiis.finalproject.dto.response.units.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class UnitsControllerTest extends BaseControllerTest {

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
        assertOk(controller.insert(req));
    }

    @Test
    void testGetAll() {
        when(businessUnits.getAll()).thenReturn(emptyMap());
        assertOk(controller.getAll());
    }

    @Test
    void testGetById() {
        when(businessUnits.getById("u1")).thenReturn(emptyMap());
        assertOk(controller.getById("u1"));
    }

    @Test
    void testDeleteById() {
        when(businessUnits.deleteById("u1")).thenReturn(new ResponseUnitsDeleteById());
        assertOk(controller.deleteById("u1"));
    }

    @Test
    void testUpdate() {
        RequestUnitsUpdate req = new RequestUnitsUpdate();
        when(businessUnits.update("u1", req)).thenReturn(new ResponseUnitsUpdate());
        assertOk(controller.insert("u1", req));
    }
}
