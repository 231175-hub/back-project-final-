package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.schoolfile.RequestSchoolFileInsert;
import com.epiis.finalproject.dto.response.school.ResponseSchoolFileInsert;
import com.epiis.finalproject.entity.EntitySchool;
import com.epiis.finalproject.entity.EntitySchoolfile;
import com.epiis.finalproject.repository.RepositorySchool;
import com.epiis.finalproject.repository.RepositorySchoolFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BusinessSchoolFileTest {

    private RepositorySchoolFile repositorySchoolFile;
    private RepositorySchool repositorySchool;
    private BusinessSchoolFile businessSchoolFile;

    @BeforeEach
    void setUp() {
        repositorySchoolFile = mock(RepositorySchoolFile.class);
        repositorySchool = mock(RepositorySchool.class);
        businessSchoolFile = new BusinessSchoolFile(repositorySchoolFile, repositorySchool);
    }

    @Test
    void testInsertSchoolNotFound() throws Exception {
        RequestSchoolFileInsert request = new RequestSchoolFileInsert();
        request.setIdSchool("invalid");

        when(repositorySchool.findById("invalid")).thenReturn(Optional.empty());

        ResponseSchoolFileInsert response = businessSchoolFile.insert(request);
        assertEquals("error", response.getType());
    }

    @Test
    void testInsertSuccess() throws Exception {
        RequestSchoolFileInsert request = new RequestSchoolFileInsert();
        request.setIdSchool("sch1");

        MockMultipartFile file = new MockMultipartFile("files", "test.pdf", "application/pdf", "Hello World".getBytes());
        request.setFiles(new MultipartFile[]{file});

        EntitySchool school = new EntitySchool();
        school.setIdSchool("sch1");
        school.setNameSchool("Sistemas");

        when(repositorySchool.findById("sch1")).thenReturn(Optional.of(school));

        ResponseSchoolFileInsert response = businessSchoolFile.insert(request);
        assertEquals("success", response.getType());
        verify(repositorySchoolFile, times(1)).saveAll(anyList());
    }

    @Test
    void testGetBySchool() {
        EntitySchool school = new EntitySchool();
        school.setIdSchool("sch1");

        EntitySchoolfile f1 = new EntitySchoolfile();
        f1.setIdSchoolFile("f1");
        f1.setParentSchool(school);

        when(repositorySchool.findById("sch1")).thenReturn(Optional.of(school));
        when(repositorySchoolFile.findAll()).thenReturn(List.of(f1));

        List<EntitySchoolfile> list = businessSchoolFile.getBySchool("sch1");
        assertEquals(1, list.size());
    }

    @Test
    void testDeleteById() {
        businessSchoolFile.deleteById("f1");
        verify(repositorySchoolFile, times(1)).deleteById("f1");
    }
}
