package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.unitscore.RequestUnitScoreInsert;
import com.epiis.finalproject.dto.request.unitscore.RequestUnitScoreUpdate;
import com.epiis.finalproject.dto.response.unitscore.*;
import com.epiis.finalproject.entity.EntityUnitscore;
import com.epiis.finalproject.repository.RepositoryUnitScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BusinessUnitScoreTest {

    private RepositoryUnitScore repositoryUnitscore;
    private BusinessUnitScore businessUnitScore;

    @BeforeEach
    void setUp() {
        repositoryUnitscore = mock(RepositoryUnitScore.class);
        businessUnitScore = new BusinessUnitScore(repositoryUnitscore);
    }

    @Test
    void testInsertUnitScore() {
        RequestUnitScoreInsert req = new RequestUnitScoreInsert();
        req.setScore(18.5);

        ResponseUnitScoreInsert response = businessUnitScore.insert(req);
        assertEquals("success", response.getType());
        verify(repositoryUnitscore, times(1)).save(any(EntityUnitscore.class));
    }

    @Test
    void testGetAllAndGetById() {
        when(repositoryUnitscore.findAll()).thenReturn(Collections.emptyList());
        when(repositoryUnitscore.findById("us1")).thenReturn(Optional.empty());

        Map<String, Object> all = businessUnitScore.getAll();
        assertNotNull(all.get("data"));

        Map<String, Object> byId = businessUnitScore.getById("us1");
        assertNotNull(byId.get("data"));
    }

    @Test
    void testDeleteById() {
        ResponseUnitScoreDeleteById response = businessUnitScore.deleteById("us1");
        assertEquals("success", response.getType());
        verify(repositoryUnitscore, times(1)).deleteById("us1");
    }

    @Test
    void testUpdateUnitScoreNotFound() {
        when(repositoryUnitscore.findById("us1")).thenReturn(Optional.empty());

        RequestUnitScoreUpdate req = new RequestUnitScoreUpdate();
        ResponseUnitScoreUpdate response = businessUnitScore.update("us1", req);
        assertEquals("error", response.getType());
    }
}
