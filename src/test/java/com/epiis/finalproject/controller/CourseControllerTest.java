package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessCourse;
import com.epiis.finalproject.dto.request.course.RequestCourseInsert;
import com.epiis.finalproject.dto.request.course.RequestCourseUpdate;
import com.epiis.finalproject.dto.response.course.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.mockito.Mockito.*;

class CourseControllerTest extends BaseControllerTest {

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
        when(businessCourse.insert(req)).thenReturn(new ResponseCourseInsert());
        assertOk(controller.insert(req));
    }

    @Test
    void testGetAll() {
        when(businessCourse.getAll()).thenReturn(emptyMap());
        assertOk(controller.getAll());
    }

    @Test
    void testGetById() {
        when(businessCourse.getById("c1")).thenReturn(emptyMap());
        assertOk(controller.getById("c1"));
    }

    @Test
    void testDeleteById() {
        when(businessCourse.deleteById("c1")).thenReturn(new ResponseCourseDeleteById());
        assertOk(controller.deleteById("c1"));
    }

    @Test
    void testUpdate() {
        RequestCourseUpdate req = new RequestCourseUpdate();
        when(businessCourse.update("c1", req)).thenReturn(new ResponseCourseUpdate());
        assertOk(controller.update("c1", req));
    }

    @Test
    void testSearchCourses() {
        when(businessCourse.searchCoursesForAutocomplete("alg", "s1")).thenReturn(Collections.emptyList());
        assertOk(controller.searchCourse("alg", "s1"));
    }
}
