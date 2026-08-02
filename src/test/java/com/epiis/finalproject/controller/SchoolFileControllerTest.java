package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessSchoolFile;
import com.epiis.finalproject.dto.request.schoolfile.RequestSchoolFileInsert;
import com.epiis.finalproject.dto.response.school.ResponseSchoolFileInsert;
import com.epiis.finalproject.entity.EntitySchoolfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SchoolFileControllerTest {

    private BusinessSchoolFile businessSchoolFile;
    private SchoolFileController controller;

    @BeforeEach
    void setUp() {
        businessSchoolFile = mock(BusinessSchoolFile.class);
        controller = new SchoolFileController(businessSchoolFile);
    }

    @Test
    void testInsert() throws Exception {
        RequestSchoolFileInsert req = new RequestSchoolFileInsert();
        ResponseSchoolFileInsert resp = new ResponseSchoolFileInsert();
        resp.success();
        when(businessSchoolFile.insert(req)).thenReturn(resp);

        ResponseEntity<ResponseSchoolFileInsert> result = controller.insert(req);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetBySchool() {
        when(businessSchoolFile.getBySchool("s1")).thenReturn(Collections.emptyList());

        ResponseEntity<List<EntitySchoolfile>> result = controller.getBySchool("s1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testDeleteById() {
        doNothing().when(businessSchoolFile).deleteById("f1");

        ResponseEntity<Void> result = controller.deleteById("f1");
        assertEquals(200, result.getStatusCode().value());
    }
}
