package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.student.RequestStudentInsert;
import com.epiis.finalproject.dto.request.student.RequestStudentUpdate;
import com.epiis.finalproject.dto.request.user.RequestUserUpdatePassword;
import com.epiis.finalproject.dto.response.student.*;
import com.epiis.finalproject.dto.response.user.ResponseUserUpdatePassword;
import com.epiis.finalproject.entity.*;
import com.epiis.finalproject.repository.*;
import com.epiis.finalproject.staticdata.EnumRoles;
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
    void testInsertStudentRoleNotFound() {
        RequestStudentInsert req = new RequestStudentInsert();
        when(repositoryRole.findByNameRole(EnumRoles.STUDENT.toString())).thenReturn(Optional.empty());

        ResponseStudentInsert response = businessStudent.insert(req);
        assertEquals("error", response.getType());
    }

    @Test
    void testInsertStudentSchoolNotFound() {
        RequestStudentInsert req = new RequestStudentInsert();
        req.setIdSchool("sch1");
        when(repositoryRole.findByNameRole(EnumRoles.STUDENT.toString())).thenReturn(Optional.of(new EntityRole()));
        when(repositorySchool.findById("sch1")).thenReturn(Optional.empty());

        ResponseStudentInsert response = businessStudent.insert(req);
        assertEquals("error", response.getType());
    }

    @Test
    void testInsertStudentSuccess() {
        RequestStudentInsert req = new RequestStudentInsert();
        req.setIdSchool("sch1");
        req.setEmail("student@test.com");
        req.setCode("ST001");
        req.setFirstName("Juan");
        req.setSurName("Perez");
        req.setPassword("pass123");

        when(repositoryRole.findByNameRole(EnumRoles.STUDENT.toString())).thenReturn(Optional.of(new EntityRole()));
        when(repositorySchool.findById("sch1")).thenReturn(Optional.of(new EntitySchool()));
        when(repositoryUser.findByEmail("student@test.com")).thenReturn(Optional.empty());
        when(repositoryStudent.findByCode("ST001")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");

        ResponseStudentInsert response = businessStudent.insert(req);
        assertEquals("success", response.getType());
        verify(entityManager, times(1)).persist(any(EntityUser.class));
    }

    @Test
    void testGetAllAndGetById() {
        when(repositoryStudent.studentsWithSchoolAndUser()).thenReturn(Collections.emptyList());
        EntityStudent s = new EntityStudent();
        s.setIdStudent("st1");
        s.setCode("ST001");
        EntitySchool sch = new EntitySchool();
        sch.setIdSchool("sch1");
        sch.setNameSchool("Escuela de Sistemas");
        s.setParentSchool(sch);

        EntityUser user = new EntityUser();
        user.setIdUser("st1");
        user.setFirstName("Carlos");
        s.setParentUser(user);

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
    void testUpdateStudentSuccess() {
        EntityStudent student = new EntityStudent();
        student.setIdStudent("st1");
        when(repositoryStudent.findById("st1")).thenReturn(Optional.of(student));

        RequestStudentUpdate updateReq = new RequestStudentUpdate();
        updateReq.setFirstName("NewFirst");
        updateReq.setSurName("NewSur");
        updateReq.setEmail("new@test.com");
        updateReq.setCode("ST002");

        ResponseStudentUpdate response = businessStudent.update("st1", updateReq);
        assertEquals("success", response.getType());
        verify(repositoryStudent, times(1)).save(any(EntityStudent.class));
    }

    @Test
    void testUpdatePassword() {
        EntityUser user = new EntityUser();
        user.setEmail("test@test.com");
        when(repositoryUser.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        RequestUserUpdatePassword req = new RequestUserUpdatePassword();
        req.setPassword("newpass");

        ResponseUserUpdatePassword response = businessStudent.updatePassword("test@test.com", req);
        assertEquals("success", response.getType());

        when(repositoryUser.findByEmail("notFound@test.com")).thenReturn(Optional.empty());
        ResponseUserUpdatePassword responseError = businessStudent.updatePassword("notFound@test.com", req);
        assertEquals("error", responseError.getType());
    }

    @Test
    void testSearchStudentsForAutocomplete() {
        List<ResponseStudentSearch> listNull = businessStudent.searchStudentsForAutocomplete("juan", null);
        assertTrue(listNull.isEmpty());

        EntityStudent st = new EntityStudent();
        st.setIdStudent("st1");
        st.setCode("ST001");
        EntityUser u = new EntityUser();
        u.setFirstName("Juan");
        u.setSurName("Perez");
        st.setParentUser(u);

        when(repositoryStudent.searchStudentsByTermAndSchool(eq("juan"), eq("sch1"), any())).thenReturn(List.of(st));
        List<ResponseStudentSearch> list = businessStudent.searchStudentsForAutocomplete("juan", "sch1");
        assertEquals(1, list.size());
    }

    @Test
    void testGetGradesByGroup() {
        EntityStudent s = new EntityStudent();
        s.setIdStudent("st1");
        s.setCode("ST001");

        EntityGroup group = new EntityGroup();
        group.setIdGroup("g1");
        group.setConceptualWeight(0.4);
        group.setPracticalWeight(0.4);
        group.setAttitudinalWeight(0.2);

        EntityGroupStudent gs = new EntityGroupStudent();
        gs.setIdGroupStudent("gs1");
        gs.setParentGroup(group);
        gs.setParentStudent(s);

        EntityUnits u1 = new EntityUnits();
        u1.setIdUnits("u1");
        u1.setNumberUnit(1);

        EntityUnitscore score = new EntityUnitscore();
        score.setParentUnits(u1);
        score.setConceptualScore(16.0);
        score.setPracticalScore(14.0);
        score.setAttitudinalScore(18.0);
        score.setScore(15.6);
        gs.setChildUnitscore(List.of(score));

        EntityAttendance att = new EntityAttendance();
        att.setStatus("A");

        when(repositoryStudent.findById("st1")).thenReturn(Optional.of(s));
        when(repositoryGroup.findById("g1")).thenReturn(Optional.of(group));
        when(repositoryGroupStudent.findByParentGroupAndParentStudent(group, s)).thenReturn(Optional.of(gs));
        when(repositoryUnits.findByParentGroupOrderByNumberUnitAsc(group)).thenReturn(List.of(u1));
        when(repositoryAttendance.findByParentGroupStudentIn(any())).thenReturn(List.of(att));

        Map<String, Object> result = businessStudent.getGradesByGroup("st1", "g1");
        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("notas"));
        assertNotNull(result.get("asistencia"));
    }

    @Test
    void testGetGradesByGroupIncompleteAndNullScore() {
        EntityStudent s = new EntityStudent();
        s.setIdStudent("st1");

        EntityGroup group = new EntityGroup();
        group.setIdGroup("g1");
        group.setConceptualWeight(0.4);
        group.setPracticalWeight(0.4);
        group.setAttitudinalWeight(0.2);

        EntityGroupStudent gs = new EntityGroupStudent();
        gs.setIdGroupStudent("gs1");
        gs.setParentGroup(group);
        gs.setParentStudent(s);

        EntityUnits u1 = new EntityUnits();
        u1.setIdUnits("u1");
        u1.setNumberUnit(1);

        EntityUnits u2 = new EntityUnits();
        u2.setIdUnits("u2");
        u2.setNumberUnit(2);

        EntityUnitscore scoreIncomplete = new EntityUnitscore();
        scoreIncomplete.setParentUnits(u1);
        scoreIncomplete.setConceptualScore(15.0);
        scoreIncomplete.setPracticalScore(null);
        scoreIncomplete.setAttitudinalScore(16.0);
        scoreIncomplete.setScore(15.5);

        gs.setChildUnitscore(List.of(scoreIncomplete));

        when(repositoryStudent.findById("st1")).thenReturn(Optional.of(s));
        when(repositoryGroup.findById("g1")).thenReturn(Optional.of(group));
        when(repositoryGroupStudent.findByParentGroupAndParentStudent(group, s)).thenReturn(Optional.of(gs));
        when(repositoryUnits.findByParentGroupOrderByNumberUnitAsc(group)).thenReturn(List.of(u1, u2));
        when(repositoryAttendance.findByParentGroupStudentIn(any())).thenReturn(Collections.emptyList());

        Map<String, Object> result = businessStudent.getGradesByGroup("st1", "g1");
        assertTrue((Boolean) result.get("success"));
    }

    @Test
    void testGetAllBoletasForStudentWithScores() {
        EntityStudent s = new EntityStudent();
        s.setIdStudent("st1");

        EntityGroupStudent gs = new EntityGroupStudent();
        gs.setIdGroupStudent("gs1");
        EntityGroup group = new EntityGroup();
        group.setIdGroup("g1");
        group.setNameGroup("Grupo 1");
        group.setConceptualWeight(0.3);
        group.setPracticalWeight(0.5);
        group.setAttitudinalWeight(0.2);

        EntityCourse course = new EntityCourse();
        course.setIdCourse("c1");
        course.setCode("CS101");
        course.setNameCourse("Programacion");
        course.setCredits(4);
        course.setCategory("OBLIGATORIO");
        group.setParentCourse(course);

        gs.setParentGroup(group);

        EntityUnits u1 = new EntityUnits();
        u1.setIdUnits("u1");
        u1.setNumberUnit(1);
        u1.setParentGroup(group);

        EntityUnitscore score = new EntityUnitscore();
        score.setParentUnits(u1);
        score.setConceptualScore(14.0);
        score.setPracticalScore(16.0);
        score.setAttitudinalScore(18.0);
        score.setScore(15.8);
        gs.setChildUnitscore(List.of(score));

        when(repositoryGroupStudent.findEnrollmentsWithGroupAndScoresByStudentId("st1")).thenReturn(List.of(gs));
        when(repositoryUnits.findByParentGroupInOrderByNumberUnitAsc(any())).thenReturn(List.of(u1));
        when(repositoryAttendance.findByParentGroupStudentIn(any())).thenReturn(Collections.emptyList());

        Map<String, Object> result = businessStudent.getAllBoletasForStudent("st1");
        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("data"));
    }
}
