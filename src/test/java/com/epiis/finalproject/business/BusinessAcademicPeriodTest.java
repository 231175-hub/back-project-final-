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
        request.setStatus("Activo");

        when(repositoryAcademicperiod.findAll()).thenReturn(Collections.emptyList());
        when(repositoryAcademicperiod.findByStatus("Activo")).thenReturn(Optional.empty());

        ResponseAcademicPeriodInsert response = businessAcademicPeriod.insert(request);
        assertEquals("success", response.getType());
        verify(repositoryAcademicperiod, times(1)).save(any(EntityAcademicPeriod.class));
    }

    @Test
    void testInsertActiveAlreadyExists() {
        RequestAcademicPeriodInsert request = new RequestAcademicPeriodInsert();
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.JANUARY, 1);
        request.setStartDate(cal.getTime());
        cal.set(2026, Calendar.JUNE, 1);
        request.setEndDate(cal.getTime());
        request.setStatus("Activo");

        when(repositoryAcademicperiod.findAll()).thenReturn(Collections.emptyList());
        when(repositoryAcademicperiod.findByStatus("Activo")).thenReturn(Optional.of(new EntityAcademicPeriod()));

        ResponseAcademicPeriodInsert response = businessAcademicPeriod.insert(request);
        assertEquals("error", response.getType());
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

    @Test
    void testUpdateSuccess() {
        EntityAcademicPeriod existing = new EntityAcademicPeriod();
        existing.setIdPeriod("p1");
        when(repositoryAcademicperiod.findById("p1")).thenReturn(Optional.of(existing));
        when(repositoryAcademicperiod.findAll()).thenReturn(Collections.emptyList());

        RequestAcademicPeriodUpdate request = new RequestAcademicPeriodUpdate();
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.JANUARY, 1);
        request.setStartDate(cal.getTime());
        cal.set(2026, Calendar.JUNE, 1);
        request.setEndDate(cal.getTime());
        request.setStatus("Activo");

        ResponseAcademicPeriodUpdate res = businessAcademicPeriod.update("p1", request);
        assertEquals("success", res.getType());
        verify(repositoryAcademicperiod, times(1)).save(any(EntityAcademicPeriod.class));
    }

    @Test
    void testInsertWithOverlapAndRomanNumerals() {
        EntityAcademicPeriod existing1 = new EntityAcademicPeriod();
        existing1.setIdPeriod("p1");
        existing1.setStatus("Activo");
        existing1.setYearPeriod(2026);
        existing1.setNumberPeriod(0);
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2026, Calendar.JANUARY, 1);
        existing1.setStartDate(cal1.getTime());
        cal1.set(2026, Calendar.JUNE, 1);
        existing1.setEndDate(cal1.getTime());

        when(repositoryAcademicperiod.findAll()).thenReturn(List.of(existing1));

        RequestAcademicPeriodInsert request = new RequestAcademicPeriodInsert();
        Calendar calReq = Calendar.getInstance();
        calReq.set(2026, Calendar.MARCH, 1);
        request.setStartDate(calReq.getTime());
        calReq.set(2026, Calendar.AUGUST, 1);
        request.setEndDate(calReq.getTime());

        ResponseAcademicPeriodInsert response = businessAcademicPeriod.insert(request);
        assertEquals("error", response.getType());
        assertTrue(response.getListMessage().get(0).contains("2026-I"));
    }

    @Test
    void testUpdateActiveConflict() {
        EntityAcademicPeriod existing = new EntityAcademicPeriod();
        existing.setIdPeriod("p1");
        when(repositoryAcademicperiod.findById("p1")).thenReturn(Optional.of(existing));
        when(repositoryAcademicperiod.findAll()).thenReturn(Collections.emptyList());

        EntityAcademicPeriod anotherActive = new EntityAcademicPeriod();
        anotherActive.setIdPeriod("p2");
        anotherActive.setStatus("Activo");
        when(repositoryAcademicperiod.findByStatus("Activo")).thenReturn(Optional.of(anotherActive));

        RequestAcademicPeriodUpdate request = new RequestAcademicPeriodUpdate();
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.JANUARY, 1);
        request.setStartDate(cal.getTime());
        cal.set(2026, Calendar.JUNE, 1);
        request.setEndDate(cal.getTime());
        request.setStatus("Activo");

        ResponseAcademicPeriodUpdate res = businessAcademicPeriod.update("p1", request);
        assertEquals("error", res.getType());
    }

    @Test
    void testUpdateValidationError() {
        EntityAcademicPeriod existing = new EntityAcademicPeriod();
        existing.setIdPeriod("p1");
        when(repositoryAcademicperiod.findById("p1")).thenReturn(Optional.of(existing));

        RequestAcademicPeriodUpdate request = new RequestAcademicPeriodUpdate();
        ResponseAcademicPeriodUpdate res = businessAcademicPeriod.update("p1", request);
        assertEquals("error", res.getType());
    }

    private void responseInsertCheckError(RequestAcademicPeriodInsert request, String expectedMessage) {
        ResponseAcademicPeriodInsert response = businessAcademicPeriod.insert(request);
        assertEquals("error", response.getType());
        assertEquals(expectedMessage, response.getListMessage().get(0));
    }
}
