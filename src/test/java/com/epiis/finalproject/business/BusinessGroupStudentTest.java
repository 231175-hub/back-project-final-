package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.groupstudent.RequestGroupStudentUpdate;
import com.epiis.finalproject.dto.request.groupstudent.RequestGroupStundentInsert;
import com.epiis.finalproject.dto.response.group.ResponseGroupDeleteById;
import com.epiis.finalproject.dto.response.groupstudent.*;
import com.epiis.finalproject.entity.EntityGroup;
import com.epiis.finalproject.entity.EntityGroupStudent;
import com.epiis.finalproject.repository.RepositoryGroup;
import com.epiis.finalproject.repository.RepositoryGroupStudent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BusinessGroupStudentTest {

    private RepositoryGroupStudent repositoryGroupStudent;
    private RepositoryGroup repositoryGroup;
    private BusinessGroupStudent businessGroupStudent;

    @BeforeEach
    void setUp() {
        repositoryGroupStudent = mock(RepositoryGroupStudent.class);
        repositoryGroup = mock(RepositoryGroup.class);
        businessGroupStudent = new BusinessGroupStudent(repositoryGroupStudent, repositoryGroup);
    }

    @Test
    void testInsertGroupStudent() {
        RequestGroupStundentInsert req = new RequestGroupStundentInsert();
        req.setIdCourse("c1");
        req.setIdStudent("s1");

        EntityGroup group = new EntityGroup();
        group.setIdGroup("g1");
        when(repositoryGroup.findGroupWithLessStudents("c1")).thenReturn(group);

        ResponseGroupStudentInsert response = businessGroupStudent.insert(req);
        assertEquals("success", response.getType());
        verify(repositoryGroupStudent, times(1)).save(any(EntityGroupStudent.class));
    }

    @Test
    void testGetAllAndGetById() {
        when(repositoryGroupStudent.findAll()).thenReturn(Collections.emptyList());
        when(repositoryGroupStudent.findById("gs1")).thenReturn(Optional.empty());

        Map<String, Object> all = businessGroupStudent.getAll();
        assertNotNull(all.get("data"));

        Map<String, Object> byId = businessGroupStudent.getById("gs1");
        assertNotNull(byId.get("data"));
    }

    @Test
    void testDeleteById() {
        ResponseGroupDeleteById response = businessGroupStudent.deleteById("gs1");
        assertEquals("success", response.getType());
        verify(repositoryGroupStudent, times(1)).deleteById("gs1");
    }

    @Test
    void testUpdateGroupStudentNotFound() {
        when(repositoryGroupStudent.findById("gs1")).thenReturn(Optional.empty());

        RequestGroupStudentUpdate req = new RequestGroupStudentUpdate();
        ResponseGroupStudentUpdate response = businessGroupStudent.update("gs1", req);
        assertEquals("error", response.getType());
    }
}
