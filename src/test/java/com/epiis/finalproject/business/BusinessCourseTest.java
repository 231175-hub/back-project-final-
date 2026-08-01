package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.course.RequestCourseInsert;
import com.epiis.finalproject.dto.request.course.RequestCourseUpdate;
import com.epiis.finalproject.dto.response.course.*;
import com.epiis.finalproject.entity.EntityCourse;
import com.epiis.finalproject.repository.RepositoryCourse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BusinessCourseTest {

    private RepositoryCourse repositoryCourse;
    private BusinessCourse businessCourse;

    @BeforeEach
    void setUp() {
        repositoryCourse = mock(RepositoryCourse.class);
        businessCourse = new BusinessCourse(repositoryCourse);
    }

    @Test
    void testInsertCourse() {
        RequestCourseInsert request = new RequestCourseInsert();
        request.setCode("IF101");
        request.setNameCourse("Algoritmos");
        request.setCategory("Obligatorio");
        request.setCredits(4);
        request.setIdSchool("s1");

        ResponseCourseInsert response = businessCourse.insert(request);
        assertEquals("success", response.getType());
        verify(repositoryCourse, times(1)).save(any(EntityCourse.class));
    }

    @Test
    void testGetAllAndGetById() {
        when(repositoryCourse.findAll()).thenReturn(Collections.emptyList());
        when(repositoryCourse.findById("c1")).thenReturn(Optional.empty());

        Map<String, Object> all = businessCourse.getAll();
        assertNotNull(all.get("data"));

        Map<String, Object> byId = businessCourse.getById("c1");
        assertNotNull(byId.get("data"));
    }

    @Test
    void testDeleteById() {
        ResponseCourseDeleteById res = businessCourse.deleteById("c1");
        assertEquals("success", res.getType());
        verify(repositoryCourse, times(1)).deleteById("c1");
    }

    @Test
    void testUpdateCourseSuccess() {
        EntityCourse course = new EntityCourse();
        course.setIdCourse("c1");
        when(repositoryCourse.findById("c1")).thenReturn(Optional.of(course));

        RequestCourseUpdate req = new RequestCourseUpdate();
        req.setCode("IF102");
        req.setNameCourse("Estructuras de Datos");

        ResponseCourseUpdate response = businessCourse.update("c1", req);
        assertEquals("success", response.getType());
        verify(repositoryCourse, times(1)).save(any(EntityCourse.class));
    }

    @Test
    void testUpdateCourseNotFound() {
        when(repositoryCourse.findById("nonexistent")).thenReturn(Optional.empty());

        RequestCourseUpdate req = new RequestCourseUpdate();
        ResponseCourseUpdate response = businessCourse.update("nonexistent", req);
        assertEquals("error", response.getType());
    }

    @Test
    void testSearchCoursesForAutocomplete() {
        List<ResponseCourseSearch> emptyResult = businessCourse.searchCoursesForAutocomplete("alg", null);
        assertTrue(emptyResult.isEmpty());

        EntityCourse course = new EntityCourse();
        course.setIdCourse("c1");
        course.setCode("IF101");
        course.setNameCourse("Algoritmos");
        course.setCredits(4);
        course.setCategory("Obligatorio");

        when(repositoryCourse.searchCoursesByTermAndSchool(eq("alg"), eq("s1"), any(PageRequest.class)))
                .thenReturn(List.of(course));

        List<ResponseCourseSearch> results = businessCourse.searchCoursesForAutocomplete("alg", "s1");
        assertEquals(1, results.size());
        assertEquals("IF101", results.get(0).code());
    }
}
