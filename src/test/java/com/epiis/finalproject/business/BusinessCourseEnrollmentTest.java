package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.courseenrollment.RequestCourseEnrollmentInsert;
import com.epiis.finalproject.dto.response.courseenrollment.ResponseCourseEnrollmentInsert;
import com.epiis.finalproject.entity.EntityAcademicPeriod;
import com.epiis.finalproject.repository.RepositoryAcademicperiod;
import com.epiis.finalproject.repository.RepositoryCourse;
import com.epiis.finalproject.repository.RepositoryCourseEnrollment;
import com.epiis.finalproject.repository.RepositoryStudent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BusinessCourseEnrollmentTest {

    private RepositoryCourseEnrollment repositoryCourseEnrollment;
    private RepositoryAcademicperiod repositoryAcademicperiod;
    private RepositoryStudent repositoryStudent;
    private RepositoryCourse repositoryCourse;

    private BusinessCourseEnrollment businessCourseEnrollment;

    @BeforeEach
    void setUp() {
        repositoryCourseEnrollment = mock(RepositoryCourseEnrollment.class);
        repositoryAcademicperiod = mock(RepositoryAcademicperiod.class);
        repositoryStudent = mock(RepositoryStudent.class);
        repositoryCourse = mock(RepositoryCourse.class);

        businessCourseEnrollment = new BusinessCourseEnrollment(
                repositoryCourseEnrollment, repositoryAcademicperiod, repositoryStudent, repositoryCourse
        );
    }

    @Test
    void testInsertNoActiveAcademicPeriod() {
        when(repositoryAcademicperiod.findByStatus(anyString())).thenReturn(Optional.empty());

        RequestCourseEnrollmentInsert req = new RequestCourseEnrollmentInsert();
        ResponseCourseEnrollmentInsert response = businessCourseEnrollment.insert(req);

        assertEquals("error", response.getType());
        assertTrue(response.getListMessage().get(0).contains("No hay un periodo académico activo"));
    }

    @Test
    void testInsertWithActivePeriod() {
        EntityAcademicPeriod activePeriod = new EntityAcademicPeriod();
        activePeriod.setIdPeriod("p1");
        when(repositoryAcademicperiod.findByStatus(anyString())).thenReturn(Optional.of(activePeriod));
        when(repositoryStudent.findById("s1")).thenReturn(Optional.of(new com.epiis.finalproject.entity.EntityStudent()));
        when(repositoryCourse.findById("c1")).thenReturn(Optional.of(new com.epiis.finalproject.entity.EntityCourse()));

        RequestCourseEnrollmentInsert req = new RequestCourseEnrollmentInsert();
        req.setIdStudent("s1");
        req.setCourses(java.util.List.of("c1"));

        ResponseCourseEnrollmentInsert response = businessCourseEnrollment.insert(req);
        assertEquals("success", response.getType());
    }
}
