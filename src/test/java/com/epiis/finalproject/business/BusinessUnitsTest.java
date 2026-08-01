package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.units.RequestUnitsInsert;
import com.epiis.finalproject.dto.request.units.RequestUnitsUpdate;
import com.epiis.finalproject.dto.response.units.*;
import com.epiis.finalproject.entity.EntityUnits;
import com.epiis.finalproject.repository.RepositoryUnits;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BusinessUnitsTest {

    private RepositoryUnits repositoryUnits;
    private BusinessUnits businessUnits;

    @BeforeEach
    void setUp() {
        repositoryUnits = mock(RepositoryUnits.class);
        businessUnits = new BusinessUnits(repositoryUnits);
    }

    @Test
    void testInsertUnits() {
        RequestUnitsInsert req = new RequestUnitsInsert();
        req.setNumberUnit(1);
        req.setNameUnit("Unidad 1");
        req.setConceptualWeight(40);
        req.setPracticalWeight(40);
        req.setAttitudinalWeight(20);

        ResponseUnitsInsert response = businessUnits.insert(req);
        assertEquals("success", response.getType());
        verify(repositoryUnits, times(1)).save(any(EntityUnits.class));
    }

    @Test
    void testGetAllAndGetById() {
        when(repositoryUnits.findAll()).thenReturn(Collections.emptyList());
        when(repositoryUnits.findById("u1")).thenReturn(Optional.empty());

        Map<String, Object> all = businessUnits.getAll();
        assertNotNull(all.get("data"));

        Map<String, Object> byId = businessUnits.getById("u1");
        assertNotNull(byId.get("data"));
    }

    @Test
    void testDeleteById() {
        ResponseUnitsDeleteById response = businessUnits.deleteById("u1");
        assertEquals("success", response.getType());
        verify(repositoryUnits, times(1)).deleteById("u1");
    }

    @Test
    void testUpdateUnitsNotFound() {
        when(repositoryUnits.findById("u1")).thenReturn(Optional.empty());

        RequestUnitsUpdate req = new RequestUnitsUpdate();
        ResponseUnitsUpdate response = businessUnits.update("u1", req);
        assertEquals("error", response.getType());
    }

    @Test
    void testUpdateUnitsSuccess() {
        EntityUnits unit = new EntityUnits();
        unit.setIdUnits("u1");
        when(repositoryUnits.findById("u1")).thenReturn(Optional.of(unit));

        RequestUnitsUpdate req = new RequestUnitsUpdate();
        req.setNameUnit("Unidad Actualizada");

        ResponseUnitsUpdate response = businessUnits.update("u1", req);
        assertEquals("success", response.getType());
        verify(repositoryUnits, times(1)).save(any(EntityUnits.class));
    }
}
