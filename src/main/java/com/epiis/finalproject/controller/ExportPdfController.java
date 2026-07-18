package com.epiis.finalproject.controller;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import com.epiis.finalproject.business.BusinessExportPdf;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'STUDENT')")
@RestController
@RequestMapping(path = "intranet")
public class ExportPdfController {
	private final BusinessExportPdf businessExportPdf;
	
	public ExportPdfController(BusinessExportPdf businessExportPdf) {
		this.businessExportPdf = businessExportPdf;
	}
	
	@Operation(
        summary = "Descargar Constancia de Matrícula", 
        description = "Genera y descarga en formato PDF la constancia oficial del semestre actual del estudiante autenticado."
    )
	@ApiResponse(
        responseCode = "200", 
        description = "PDF generado exitosamente", 
        content = @Content(
            mediaType = MediaType.APPLICATION_PDF_VALUE, 
            schema = @Schema(type = "string", format = "binary") 
        )
    )
	@GetMapping("/downloadscorepdf")
	public ResponseEntity<byte[]> downloadConstancia(@AuthenticationPrincipal Jwt jwt) {
        String idStudentKeycloak = jwt.getSubject();
        
        byte[] pdfBytes = businessExportPdf.generatePdf(idStudentKeycloak);
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=Constancia_Matricula.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
	}
	
	@Operation(
        summary = "Descargar Historial Académico", 
        description = "Genera y descarga en formato PDF el historial académico completo del estudiante autenticado."
    )
	@ApiResponse(
        responseCode = "200", 
        description = "PDF generado exitosamente", 
        content = @Content(
            mediaType = MediaType.APPLICATION_PDF_VALUE, 
            schema = @Schema(type = "string", format = "binary") 
        )
    )
	@GetMapping("/downloadrecordpdf")
	public ResponseEntity<byte[]> downloadHistorial(@AuthenticationPrincipal Jwt jwt) {
        String idStudentKeycloak = jwt.getSubject();
        
        byte[] pdfBytes = businessExportPdf.generateHistorialPdf(idStudentKeycloak);
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=Historial_Academico.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
	}
	
	@GetMapping("/downloadschedulepdf")
	public ResponseEntity<byte[]> downloadSchedule(@AuthenticationPrincipal Jwt jwt) {
        String idStudentKeycloak = jwt.getSubject();
        
        byte[] pdfBytes = businessExportPdf.generateSchedulePdf(idStudentKeycloak);
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=Horario_Academico_UNAMBA.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
	}
}
