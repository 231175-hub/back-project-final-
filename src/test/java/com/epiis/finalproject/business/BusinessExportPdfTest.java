package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.response.schedule.ResponseScheduleGetAll;
import com.epiis.finalproject.entity.*;
import com.epiis.finalproject.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BusinessExportPdfTest {

    private BusinessStudent businessStudent;
    private TemplateEngine templateEngine;
    private RepositoryStudent repositoryStudent;
    private RepositoryAcademicperiod repositoryAcademicPeriod;
    private RepositoryGroupStudent repositoryGroupStudent;
    private RepositoryCourseEnrollment repositoryCourseEnrollment;
    private RepositoryUser repositoryUser;

    private BusinessExportPdf businessExportPdf;

    @BeforeEach
    void setUp() {
        businessStudent = mock(BusinessStudent.class);
        templateEngine = mock(TemplateEngine.class);
        repositoryStudent = mock(RepositoryStudent.class);
        repositoryAcademicPeriod = mock(RepositoryAcademicperiod.class);
        repositoryGroupStudent = mock(RepositoryGroupStudent.class);
        repositoryCourseEnrollment = mock(RepositoryCourseEnrollment.class);
        repositoryUser = mock(RepositoryUser.class);

        businessExportPdf = new BusinessExportPdf(
                businessStudent, templateEngine, repositoryStudent,
                repositoryAcademicPeriod, repositoryGroupStudent,
                repositoryCourseEnrollment, repositoryUser
        );
    }

    @Test
    void testGenerateHistorialPdfStudentNotFound() {
        when(repositoryStudent.findById("st1")).thenReturn(Optional.empty());
        when(repositoryUser.findFirstByEmail("st1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> businessExportPdf.generateHistorialPdf("st1"));
    }

    @Test
    void testGeneratePdfSuccess() {
        EntityStudent s = new EntityStudent();
        s.setIdStudent("st1");
        s.setCode("20201010");

        EntityUser u = new EntityUser();
        u.setFirstName("Pedro");
        u.setSurName("Perez");
        s.setParentUser(u);

        EntitySchool sch = new EntitySchool();
        sch.setNameSchool("Ingeniería Informática");
        s.setParentSchool(sch);

        when(repositoryStudent.findById("st1")).thenReturn(Optional.of(s));

        Map<String, Object> mockReportData = new HashMap<>();
        when(businessStudent.getAllBoletasForStudent("st1")).thenReturn(mockReportData);
        when(templateEngine.process(eq("constancia"), any(Context.class))).thenReturn("<html><body>Test</body></html>");

        byte[] pdf = businessExportPdf.generatePdf("st1");
        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    @Test
    void testGenerateHistorialPdfSuccess() {
        EntityStudent s = new EntityStudent();
        s.setIdStudent("st1");
        s.setCode("20201010");

        EntityUser u = new EntityUser();
        u.setFirstName("Pedro");
        u.setSurName("Perez");
        s.setParentUser(u);

        EntitySchool sch = new EntitySchool();
        sch.setNameSchool("Ingeniería Informática");
        s.setParentSchool(sch);

        when(repositoryStudent.findById("st1")).thenReturn(Optional.of(s));

        EntityCourseEnrollment enrollment = new EntityCourseEnrollment();
        enrollment.setStatus("APROBADO");
        enrollment.setFinalScore(18);

        EntityCourse course = new EntityCourse();
        course.setCode("CS101");
        course.setNameCourse("Programacion I");
        course.setCategory("Obligatorio");
        course.setCredits(4);
        enrollment.setParentCourse(course);

        EntityAcademicPeriod period = new EntityAcademicPeriod();
        period.setYearPeriod(2026);
        period.setNumberPeriod(1);
        enrollment.setParentPeriod(period);

        when(repositoryCourseEnrollment.findHistorialByStudent("st1")).thenReturn(List.of(enrollment));
        when(templateEngine.process(eq("historial"), any(Context.class))).thenReturn("<html><body>Historial</body></html>");

        byte[] pdf = businessExportPdf.generateHistorialPdf("st1");
        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    @Test
    void testGenerateSchedulePdfSuccess() {
        EntityStudent s = new EntityStudent();
        s.setIdStudent("st1");
        s.setCode("20201010");

        EntityUser u = new EntityUser();
        u.setFirstName("Pedro");
        u.setSurName("Perez");
        s.setParentUser(u);

        when(repositoryStudent.findById("st1")).thenReturn(Optional.of(s));

        EntityAcademicPeriod activePeriod = new EntityAcademicPeriod();
        activePeriod.setYearPeriod(2026);
        activePeriod.setNumberPeriod(1);
        when(repositoryAcademicPeriod.findByStatus("Activo")).thenReturn(Optional.of(activePeriod));

        ResponseScheduleGetAll schedule1 = new ResponseScheduleGetAll("Lunes", "08:00", "10:00", "Lab 1", "CS101", "01");
        ResponseScheduleGetAll schedule2 = new ResponseScheduleGetAll("Lunes", "09:00", "11:00", "Lab 2", "CS102", "02");
        when(repositoryGroupStudent.findCustomScheduleByStudentId("st1")).thenReturn(List.of(schedule1, schedule2));

        when(templateEngine.process(eq("horario"), any(Context.class))).thenReturn("<html><body>Horario</body></html>");

        byte[] pdf = businessExportPdf.generateSchedulePdf("st1");
        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    @Test
    void testGeneratePdfByUserEmail() {
        EntityUser u = new EntityUser();
        u.setIdUser("st1");
        u.setEmail("test@email.com");
        EntityStudent s = new EntityStudent();
        s.setIdStudent("st1");
        s.setParentUser(u);
        u.setChildStudent(s);

        when(repositoryUser.findFirstByEmail("test@email.com")).thenReturn(Optional.of(u));
        when(repositoryStudent.findById("st1")).thenReturn(Optional.of(s));
        when(businessStudent.getAllBoletasForStudent("st1")).thenReturn(new HashMap<>());
        when(templateEngine.process(eq("constancia"), any(Context.class))).thenReturn("<html><body>Test</body></html>");

        byte[] pdf = businessExportPdf.generatePdfByUserEmail("test@email.com");
        assertNotNull(pdf);
    }

    @Test
    void testGenerateHistorialPdfByUserEmail() {
        EntityUser u = new EntityUser();
        u.setIdUser("st1");
        u.setEmail("test@email.com");
        EntityStudent s = new EntityStudent();
        s.setIdStudent("st1");
        s.setParentUser(u);
        u.setChildStudent(s);

        when(repositoryUser.findFirstByEmail("test@email.com")).thenReturn(Optional.of(u));
        when(repositoryStudent.findById("st1")).thenReturn(Optional.of(s));
        when(repositoryCourseEnrollment.findHistorialByStudent("st1")).thenReturn(Collections.emptyList());
        when(templateEngine.process(eq("historial"), any(Context.class))).thenReturn("<html><body>Historial</body></html>");

        byte[] pdf = businessExportPdf.generateHistorialPdfByUserEmail("test@email.com");
        assertNotNull(pdf);
    }

    @Test
    void testGenerateSchedulePdfByUserEmail() {
        EntityUser u = new EntityUser();
        u.setIdUser("st1");
        u.setEmail("test@email.com");
        EntityStudent s = new EntityStudent();
        s.setIdStudent("st1");
        s.setParentUser(u);
        u.setChildStudent(s);

        when(repositoryUser.findFirstByEmail("test@email.com")).thenReturn(Optional.of(u));
        when(repositoryStudent.findById("st1")).thenReturn(Optional.of(s));
        when(repositoryGroupStudent.findCustomScheduleByStudentId("st1")).thenReturn(Collections.emptyList());
        when(templateEngine.process(eq("horario"), any(Context.class))).thenReturn("<html><body>Horario</body></html>");

        byte[] pdf = businessExportPdf.generateSchedulePdfByUserEmail("test@email.com");
        assertNotNull(pdf);
    }
}
