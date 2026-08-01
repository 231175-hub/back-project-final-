package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.student.RequestStudentInsert;
import com.epiis.finalproject.dto.request.student.RequestStudentUpdate;
import com.epiis.finalproject.dto.response.student.*;
import com.epiis.finalproject.entity.*;
import com.epiis.finalproject.repository.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BusinessStudentTest {

    private RepositoryStudent repositoryStudent;
    private RepositoryUser repositoryUser;
    private RepositoryRole repositoryRole;
    private RepositorySchool repositorySchool;
    private PasswordEncoder passwordEncoder;
    private RepositoryGroup repositoryGroup;
    private RepositoryGroupStudent repositoryGroupStudent;
    private RepositoryUnits repositoryUnits;
    private RepositoryAttendance repositoryAttendance;
    private EntityManager entityManager;

    private BusinessStudent businessStudent;

    @BeforeEach
    void setUp() {
        repositoryStudent = mock(RepositoryStudent.class);
        repositoryUser = mock(RepositoryUser.class);
        repositoryRole = mock(RepositoryRole.class);
        repositorySchool = mock(RepositorySchool.class);
        passwordEncoder = mock(PasswordEncoder.class);
        repositoryGroup = mock(RepositoryGroup.class);
        repositoryGroupStudent = mock(RepositoryGroupStudent.class);
        repositoryUnits = mock(RepositoryUnits.class);
        repositoryAttendance = mock(RepositoryAttendance.class);
        entityManager = mock(EntityManager.class);

        businessStudent = new BusinessStudent(
                repositoryStudent, repositoryUser, repositoryRole, repositorySchool,
                passwordEncoder, repositoryGroup, repositoryGroupStudent,
                repositoryUnits, repositoryAttendance, entityManager
        );
    }

    @Test
    void testGetAllAndGetById() {
        when(repositoryStudent.studentsWithSchoolAndUser()).thenReturn(Collections.emptyList());
        EntityStudent s = new EntityStudent();
        s.setIdStudent("st1");
        when(repositoryStudent.findByIdWithSchoolAndUser("st1")).thenReturn(Optional.of(s));

        Map<String, Object> all = businessStudent.getAll();
        assertNotNull(all.get("data"));

        Map<String, Object> byId = businessStudent.getById("st1");
        assertNotNull(byId.get("data"));
    }

    @Test
    void testDeleteById() {
        ResponseStudentDeleteById response = businessStudent.deleteById("st1");
        assertEquals("success", response.getType());
        verify(repositoryStudent, times(1)).deleteById("st1");
    }

    @Test
    void testUpdateStudentNotFound() {
        when(repositoryStudent.findById("st1")).thenReturn(Optional.empty());

        RequestStudentUpdate updateReq = new RequestStudentUpdate();
        ResponseStudentUpdate response = businessStudent.update("st1", updateReq);
        assertEquals("error", response.getType());
    }

    @Test
    void testSearchStudentsForAutocompleteNullSchool() {
        List<ResponseStudentSearch> list = businessStudent.searchStudentsForAutocomplete("juan", null);
        assertTrue(list.isEmpty());
    }
}
