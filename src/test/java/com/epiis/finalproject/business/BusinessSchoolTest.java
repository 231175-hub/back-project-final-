package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.school.RequestSchoolInsert;
import com.epiis.finalproject.dto.request.school.RequestSchoolUpdate;
import com.epiis.finalproject.dto.response.school.ResponseSchoolDeleteById;
import com.epiis.finalproject.dto.response.school.ResponseSchoolInsert;
import com.epiis.finalproject.dto.response.school.ResponseSchoolUpdate;
import com.epiis.finalproject.entity.EntitySchool;
import com.epiis.finalproject.repository.RepositorySchool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BusinessSchoolTest {

    private RepositorySchool repositorySchool;
    private BusinessSchool businessSchool;

    @BeforeEach
    void setUp() {
        repositorySchool = mock(RepositorySchool.class);
        businessSchool = new BusinessSchool(repositorySchool);
    }

    @Test
    void testInsertWithoutFile() throws IOException {
        RequestSchoolInsert request = new RequestSchoolInsert();
        request.setNameSchool("Ingenieria de Sistemas");

        ResponseSchoolInsert response = businessSchool.insert(request);
        assertEquals("success", response.getType());
        verify(repositorySchool, times(1)).save(any(EntitySchool.class));
    }

    @Test
    void testInsertWithFile() throws IOException {
        RequestSchoolInsert request = new RequestSchoolInsert();
        request.setNameSchool("Ingenieria de Sistemas Test");
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "test bytes".getBytes());
        request.setFile(file);

        ResponseSchoolInsert response = businessSchool.insert(request);
        assertEquals("success", response.getType());
        verify(repositorySchool, times(1)).save(any(EntitySchool.class));
    }

    @Test
    void testGetAllAndGetById() {
        when(repositorySchool.findAll()).thenReturn(Collections.emptyList());
        when(repositorySchool.findById("s1")).thenReturn(Optional.empty());

        Map<String, Object> all = businessSchool.getAll();
        assertNotNull(all.get("data"));

        Map<String, Object> byId = businessSchool.getById("s1");
        assertNotNull(byId.get("data"));
    }

    @Test
    void testDeleteByIdPresent() throws IOException {
        EntitySchool school = new EntitySchool();
        school.setIdSchool("s1");
        school.setNameSchool("SchoolForDelete");
        when(repositorySchool.findById("s1")).thenReturn(Optional.of(school));

        ResponseSchoolDeleteById response = businessSchool.deleteById("s1");
        assertEquals("success", response.getType());
        verify(repositorySchool, times(1)).deleteById("s1");
    }

    @Test
    void testUpdateNotFound() {
        RequestSchoolUpdate updateReq = new RequestSchoolUpdate();
        updateReq.setNameSchool("Ingenieria Agroindustrial");

        ResponseSchoolUpdate response = businessSchool.update("s1", updateReq);
        assertEquals("error", response.getType());
    }

    @Test
    void testUpdateSuccessWithFile() {
        EntitySchool school = new EntitySchool();
        school.setIdSchool("s1");
        school.setNameSchool("Sistemas");

        when(repositorySchool.findById("s1")).thenReturn(Optional.of(school));

        RequestSchoolUpdate updateReq = new RequestSchoolUpdate();
        updateReq.setNameSchool("Ingenieria de Sistemas e Informatica");
        MockMultipartFile file = new MockMultipartFile("file", "logo.png", "image/png", "logo content".getBytes());
        updateReq.setFile(file);

        ResponseSchoolUpdate response = businessSchool.update("s1", updateReq);
        assertEquals("success", response.getType());
        verify(repositorySchool, times(1)).save(any(EntitySchool.class));
    }
}
