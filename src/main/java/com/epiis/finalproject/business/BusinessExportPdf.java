package com.epiis.finalproject.business;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.epiis.finalproject.dto.response.schedule.ResponseScheduleGetAll;
import com.epiis.finalproject.entity.EntityAcademicPeriod;
import com.epiis.finalproject.entity.EntityCourseEnrollment;
import com.epiis.finalproject.entity.EntityStudent;
import com.epiis.finalproject.repository.RepositoryAcademicperiod;
import com.epiis.finalproject.repository.RepositoryCourseEnrollment;
import com.epiis.finalproject.repository.RepositoryStudent;
import com.epiis.finalproject.repository.RepositoryGroupStudent; 
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class BusinessExportPdf {

    private final BusinessStudent businessStudent;
    private final TemplateEngine templateEngine;
    private final RepositoryStudent repositoryStudent; 
    private final RepositoryAcademicperiod repositoryAcademicPeriod;
    private final RepositoryGroupStudent repositoryGroupStudent; 
    private final RepositoryCourseEnrollment repositoryCourseEnrollment;

    public BusinessExportPdf(BusinessStudent businessStudent, TemplateEngine templateEngine, RepositoryStudent repositoryStudent, RepositoryAcademicperiod repositoryAcademicPeriod,RepositoryGroupStudent repositoryGroupStudent, RepositoryCourseEnrollment repositoryCourseEnrollment) {
        this.businessStudent = businessStudent;
        this.templateEngine = templateEngine;
        this.repositoryStudent = repositoryStudent;
        this.repositoryAcademicPeriod = repositoryAcademicPeriod;
        this.repositoryGroupStudent = repositoryGroupStudent;
        this.repositoryCourseEnrollment = repositoryCourseEnrollment;
    }

    public byte[] generatePdf(String idStudentKeycloak) {
        
        Map<String, Object> data = businessStudent.getAllBoletasForStudent(idStudentKeycloak);
        EntityStudent estudiante = repositoryStudent.findById(idStudentKeycloak).orElse(null);
        
        EntityAcademicPeriod periodoActivo = repositoryAcademicPeriod.findByStatus("Activo").orElse(null);
        
        String semestreActual = "NO DEFINIDO";
        if (periodoActivo != null) {
            semestreActual = periodoActivo.getYearPeriod() + "-" + periodoActivo.getNumberPeriod();
        }

        Map<String, String> studentData = new HashMap<>();
        int totalCredits = 0;

        if (estudiante != null) {
            String fullName = estudiante.getParentUser().getSurName() + " " + estudiante.getParentUser().getFirstName();
            String career = estudiante.getParentSchool().getNameSchool();

            studentData.put("fullName", fullName.toUpperCase()); 
            studentData.put("career", career.toUpperCase());
            studentData.put("code", estudiante.getCode()); 
            studentData.put("semester", semestreActual); 
            
            totalCredits = repositoryGroupStudent.sumTotalCreditsByStudentAndActivePeriod(estudiante.getIdStudent());
        }

        Context context = new Context();
        context.setVariable("student", studentData);
        context.setVariables(data);
        context.setVariable("totalCredits", totalCredits); 

        String html = templateEngine.process("constancia", context);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, "/");
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
    
    public byte[] generateHistorialPdf(String idStudentKeycloak) {
        
        EntityStudent estudiante = repositoryStudent.findById(idStudentKeycloak).orElse(null);
        if (estudiante == null) {
            throw new RuntimeException("Estudiante no encontrado");
        }

        List<EntityCourseEnrollment> historialBD = repositoryCourseEnrollment.findHistorialByStudent(idStudentKeycloak);

        Map<String, List<Map<String, Object>>> historialAgrupado = new LinkedHashMap<>();
        int totalCreditosAprobados = 0;

        for (EntityCourseEnrollment enrollment : historialBD) {
            
            String year = String.valueOf(enrollment.getParentPeriod().getYearPeriod());
            String number = String.valueOf(enrollment.getParentPeriod().getNumberPeriod());
            String nombreSemestre = "SEMESTRE ACADEMICO " + year + "-" + number; 
            
            historialAgrupado.putIfAbsent(nombreSemestre, new ArrayList<>());

            Map<String, Object> cursoData = new HashMap<>();
            cursoData.put("codigo", enrollment.getParentCourse().getCode());
            cursoData.put("asignatura", enrollment.getParentCourse().getNameCourse());
            
            cursoData.put("nota", enrollment.getFinalScore() != null ? enrollment.getFinalScore() : "-");
            cursoData.put("categoria", enrollment.getParentCourse().getCategory());
            cursoData.put("creditos", enrollment.getParentCourse().getCredits());

            historialAgrupado.get(nombreSemestre).add(cursoData);

            if ("APROBADO".equalsIgnoreCase(enrollment.getStatus())) {
                totalCreditosAprobados += enrollment.getParentCourse().getCredits();
            }
        }

        Map<String, String> studentData = new HashMap<>();
        String fullName = estudiante.getParentUser().getSurName() + " " + estudiante.getParentUser().getFirstName();
        studentData.put("fullName", fullName.toUpperCase()); 
        studentData.put("career", estudiante.getParentSchool().getNameSchool().toUpperCase());
        studentData.put("code", estudiante.getCode());
      
        Context context = new Context();
        context.setVariable("student", studentData);
        context.setVariable("historial", historialAgrupado);
        context.setVariable("totalCreditosAprobados", totalCreditosAprobados);

        String html = templateEngine.process("historial", context); 

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, "/");
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el Historial en PDF", e);
        }
    }
    
    public byte[] generateSchedulePdf(String idStudentKeycloak) {
		
		EntityStudent estudiante = repositoryStudent.findById(idStudentKeycloak).orElse(null);
		if (estudiante == null) {
			throw new RuntimeException("Estudiante no encontrado");
		}

		List<ResponseScheduleGetAll> scheduleList = repositoryGroupStudent.findCustomScheduleByStudentId(idStudentKeycloak);

		Map<String, String> studentData = new HashMap<>();
		String fullName = estudiante.getParentUser().getSurName() + " " + estudiante.getParentUser().getFirstName();
		studentData.put("fullName", fullName.toUpperCase()); 
		studentData.put("career", estudiante.getParentSchool().getNameSchool().toUpperCase());
		studentData.put("code", estudiante.getCode());
		
		EntityAcademicPeriod periodoActivo = repositoryAcademicPeriod.findByStatus("Activo").orElse(null);
		studentData.put("semester", periodoActivo != null ? (periodoActivo.getYearPeriod() + "-" + periodoActivo.getNumberPeriod()) : "NO DEFINIDO");

		List<Integer> hours = new ArrayList<>();
		for (int i = 7; i <= 22; i++) {
			hours.add(i);
		}

		List<String> daysOfWeek = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes");

		Map<String, List<ResponseScheduleGetAll>> activeHourly = new HashMap<>();
		for (ResponseScheduleGetAll lesson : scheduleList) {
			int startHour = Integer.parseInt(lesson.startTime().split(":")[0]);
			int endHour = Integer.parseInt(lesson.endTime().split(":")[0]);

			for (int h = startHour; h < endHour; h++) {
				String key = lesson.dayWeek() + "-" + h;
				activeHourly.computeIfAbsent(key, k -> new ArrayList<>()).add(lesson);
			}
		}

		Map<String, List<ResponseScheduleGetAll>> customSchedule = new HashMap<>();
		Map<String, Boolean> skipCells = new HashMap<>();
		Map<String, Integer> durations = new HashMap<>();

		for (ResponseScheduleGetAll lesson : scheduleList) {
			int startHour = Integer.parseInt(lesson.startTime().split(":")[0]);
			int endHour = Integer.parseInt(lesson.endTime().split(":")[0]);

			boolean hasConflict = false;
			for (int h = startHour; h < endHour; h++) {
				String key = lesson.dayWeek() + "-" + h;
				List<ResponseScheduleGetAll> activeList = activeHourly.get(key);
				if (activeList != null && activeList.size() > 1) {
					hasConflict = true;
					break;
				}
			}

			if (!hasConflict) {
				String cellKey = lesson.dayWeek() + "-" + startHour;
				List<ResponseScheduleGetAll> list = new ArrayList<>();
				list.add(lesson);
				customSchedule.put(cellKey, list);
				durations.put(cellKey, endHour - startHour);

				for (int h = startHour + 1; h < endHour; h++) {
					skipCells.put(lesson.dayWeek() + "-" + h, true);
				}
			} else {
				for (int h = startHour; h < endHour; h++) {
					String cellKey = lesson.dayWeek() + "-" + h;
					if (!customSchedule.containsKey(cellKey)) {
						customSchedule.put(cellKey, activeHourly.get(cellKey));
						durations.put(cellKey, 1);
					}
				}
			}
		}

		Context context = new Context();
		context.setVariable("student", studentData);
		context.setVariable("hours", hours);
		context.setVariable("daysOfWeek", daysOfWeek);
		context.setVariable("customSchedule", customSchedule);
		context.setVariable("skipCells", skipCells);
		context.setVariable("durations", durations);

		String html = templateEngine.process("horario", context); 

		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			PdfRendererBuilder builder = new PdfRendererBuilder();
			builder.withHtmlContent(html, "/");
			builder.toStream(os);
			builder.run();
			return os.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("Error al generar el Horario en PDF", e);
		}
	}
}
