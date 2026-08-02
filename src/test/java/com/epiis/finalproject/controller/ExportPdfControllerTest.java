package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessExportPdf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExportPdfControllerTest {

    private BusinessExportPdf businessExportPdf;
    private ExportPdfController controller;
    private Principal principal;

    @BeforeEach
    void setUp() {
        businessExportPdf = mock(BusinessExportPdf.class);
        controller = new ExportPdfController(businessExportPdf);
        principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user@email.com");
    }

    @Test
    void testDownloadConstancia() {
        byte[] pdf = new byte[]{1, 2, 3};
        when(businessExportPdf.generatePdfByUserEmail("user@email.com")).thenReturn(pdf);

        ResponseEntity<byte[]> response = controller.downloadConstancia(principal);
        assertEquals(200, response.getStatusCode().value());
        assertArrayEquals(pdf, response.getBody());
    }

    @Test
    void testDownloadHistorial() {
        byte[] pdf = new byte[]{1, 2, 3};
        when(businessExportPdf.generateHistorialPdfByUserEmail("user@email.com")).thenReturn(pdf);

        ResponseEntity<byte[]> response = controller.downloadHistorial(principal);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testDownloadSchedule() {
        byte[] pdf = new byte[]{1, 2, 3};
        when(businessExportPdf.generateSchedulePdfByUserEmail("user@email.com")).thenReturn(pdf);

        ResponseEntity<byte[]> response = controller.downloadSchedule(principal);
        assertEquals(200, response.getStatusCode().value());
    }
}
