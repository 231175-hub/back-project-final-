package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessCourse;
import com.epiis.finalproject.dto.request.course.RequestCourseInsert;
import com.epiis.finalproject.dto.request.course.RequestCourseUpdate;
import com.epiis.finalproject.dto.response.course.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseControllerTest {

    private BusinessCourse businessCourse;
    private CourseController controller;

    @BeforeEach
    void setUp() {
        businessCourse = mock(BusinessCourse.class);
        controller = new CourseController(businessCourse);
    }

    @Test
    void testInsert() {
        RequestCourseInsert req = new RequestCourseInsert();
        ResponseCourseInsert resMock = new ResponseCourseInsert();
        when(businessCourse.insert(req)).thenReturn(resMock);

        ResponseEntity<ResponseCourseInsert> response = controller.insert(req);
        assertNotNull(response);
    }

    @Test
    void testGetAll() {
        Map<String, Object> mockMap = new HashMap<>();
        when(businessCourse.getAll()).thenReturn(mockMap);

        ResponseEntity<Map<String, Object>> response = controller.getAll();
        assertNotNull(response);
    }

    @Test
    void testGetById() {
        Map<String, Object> mockMap = new HashMap<>();
        when(businessCourse.getById("c1")).thenReturn(mockMap);

        ResponseEntity<Map<String, Object>> response = controller.getById("c1");
        assertNotNull(response);
    }

    @Test
    void testDeleteById() {
        ResponseCourseDeleteById resMock = new ResponseCourseDeleteById();
        when(businessCourse.deleteById("c1")).thenReturn(resMock);

        ResponseEntity<ResponseCourseDeleteById> response = controller.deleteById("c1");
        assertNotNull(response);
    }

    @Test
    void testUpdate() {
        RequestCourseUpdate req = new RequestCourseUpdate();
        ResponseCourseUpdate resMock = new ResponseCourseUpdate();
        when(businessCourse.update("c1", req)).thenReturn(resMock);

        ResponseEntity<ResponseCourseUpdate> response = controller.update("c1", req);
        assertNotNull(response);
    }

    @Test
    void testSearchCourses() {
        when(businessCourse.searchCoursesForAutocomplete("alg", "s1")).thenReturn(Collections.emptyList());

        ResponseEntity<List<ResponseCourseSearch>> response = controller.searchCourse("alg", "s1");
        assertNotNull(response);
    }
}
