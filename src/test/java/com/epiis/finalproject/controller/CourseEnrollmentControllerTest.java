package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessCourseEnrollment;
import com.epiis.finalproject.dto.request.courseenrollment.RequestCourseEnrollmentInsert;
import com.epiis.finalproject.dto.response.courseenrollment.ResponseCourseEnrollmentInsert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseEnrollmentControllerTest {

    private BusinessCourseEnrollment businessCourseEnrollment;
    private CourseEnrollmentController controller;

    @BeforeEach
    void setUp() {
        businessCourseEnrollment = mock(BusinessCourseEnrollment.class);
        controller = new CourseEnrollmentController(businessCourseEnrollment);
    }

    @Test
    void testInsert() {
        RequestCourseEnrollmentInsert req = new RequestCourseEnrollmentInsert();
        ResponseCourseEnrollmentInsert resp = new ResponseCourseEnrollmentInsert();
        resp.success();
        when(businessCourseEnrollment.insert(req)).thenReturn(resp);

        ResponseEntity<ResponseCourseEnrollmentInsert> result = controller.insert(req);
        assertEquals(200, result.getStatusCode().value());
    }
}
