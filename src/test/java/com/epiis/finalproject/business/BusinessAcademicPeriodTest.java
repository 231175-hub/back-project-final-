package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.academicperiod.RequestAcademicPeriodInsert;
import com.epiis.finalproject.dto.request.academicperiod.RequestAcademicPeriodUpdate;
import com.epiis.finalproject.dto.response.academicperiod.ResponseAcademicPeriodDeleteById;
import com.epiis.finalproject.dto.response.academicperiod.ResponseAcademicPeriodInsert;
import com.epiis.finalproject.dto.response.academicperiod.ResponseAcademicPeriodUpdate;
import com.epiis.finalproject.entity.EntityAcademicPeriod;
import com.epiis.finalproject.repository.RepositoryAcademicperiod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BusinessAcademicPeriodTest {

    private RepositoryAcademicperiod repositoryAcademicperiod;
    private BusinessAcademicPeriod businessAcademicPeriod;

    @BeforeEach
    void setUp() {
        repositoryAcademicperiod = mock(RepositoryAcademicperiod.class);
        businessAcademicPeriod = new BusinessAcademicPeriod(repositoryAcademicperiod);
    }

    @Test
    void testInsertValidationErrorNullDates() {
        RequestAcademicPeriodInsert request = new RequestAcademicPeriodInsert();
        responseInsertCheckError(request, "Las fechas de inicio y fin son obligatorias.");
    }

    @Test
    void testInsertValidationErrorStartAfterEnd() {
        RequestAcademicPeriodInsert request = new RequestAcademicPeriodInsert();
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.JANUARY, 10);
        request.setStartDate(cal.getTime());
        cal.set(2026, Calendar.JANUARY, 1);
        request.setEndDate(cal.getTime());

        responseInsertCheckError(request, "La fecha de inicio debe ser anterior a la fecha de fin.");
    }

    @Test
    void testInsertValidationErrorDurationTooShort() {
        RequestAcademicPeriodInsert request = new RequestAcademicPeriodInsert();
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.JANUARY, 1);
        request.setStartDate(cal.getTime());
        cal.set(2026, Calendar.FEBRUARY, 1);
        request.setEndDate(cal.getTime());

        ResponseAcademicPeriodInsert response = businessAcademicPeriod.insert(request);
        assertEquals("error", response.getType());
        assertTrue(response.getListMessage().get(0).contains("debe durar al menos 119 días"));
    }

    @Test
    void testInsertSuccess() {
        RequestAcademicPeriodInsert request = new RequestAcademicPeriodInsert();
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.JANUARY, 1);
        request.setStartDate(cal.getTime());
        cal.set(2026, Calendar.JUNE, 1);
        request.setEndDate(cal.getTime());
        request.setYearPeriod(2026);
        request.setNumberPeriod(1);
        request.setStatus("Finalizado");

        when(repositoryAcademicperiod.findAll()).thenReturn(Collections.emptyList());

        ResponseAcademicPeriodInsert response = businessAcademicPeriod.insert(request);
        assertEquals("success", response.getType());
        verify(repositoryAcademicperiod, times(1)).save(any(EntityAcademicPeriod.class));
    }

    @Test
    void testGetAllAndGetById() {
        when(repositoryAcademicperiod.findAll()).thenReturn(Collections.emptyList());
        when(repositoryAcademicperiod.findById("p1")).thenReturn(Optional.empty());

        Map<String, Object> all = businessAcademicPeriod.getAll();
        assertNotNull(all.get("data"));

        Map<String, Object> byId = businessAcademicPeriod.getById("p1");
        assertNotNull(byId.get("data"));
    }

    @Test
    void testDeleteById() {
        ResponseAcademicPeriodDeleteById res = businessAcademicPeriod.deleteById("p1");
        assertEquals("success", res.getType());
        verify(repositoryAcademicperiod, times(1)).deleteById("p1");
    }

    @Test
    void testUpdateNotFound() {
        RequestAcademicPeriodUpdate request = new RequestAcademicPeriodUpdate();
        ResponseAcademicPeriodUpdate res = businessAcademicPeriod.update("nonexistent", request);
        assertEquals("error", res.getType());
    }

    private void responseInsertCheckError(RequestAcademicPeriodInsert request, String expectedMessage) {
        ResponseAcademicPeriodInsert response = businessAcademicPeriod.insert(request);
        assertEquals("error", response.getType());
        assertEquals(expectedMessage, response.getListMessage().get(0));
    }
}
