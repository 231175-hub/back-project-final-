package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.groupregister.RequestGroupRegisterSave;
import com.epiis.finalproject.dto.response.groupregister.ResponseGroupRegisterData;
import com.epiis.finalproject.entity.*;
import com.epiis.finalproject.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class BusinessGroupRegisterTest {

    private RepositoryGroup repositoryGroup;
    private RepositoryGroupStudent repositoryGroupStudent;
    private RepositoryUnits repositoryUnits;
    private RepositoryUnitScore repositoryUnitScore;
    private RepositoryAttendance repositoryAttendance;
    private RepositoryCourseEnrollment repositoryCourseEnrollment;
    private RepositoryStudent repositoryStudent;
    private RepositoryGradeLog repositoryGradeLog;

    private BusinessGroupRegister businessGroupRegister;

    @BeforeEach
    void setUp() {
        repositoryGroup = mock(RepositoryGroup.class);
        repositoryGroupStudent = mock(RepositoryGroupStudent.class);
        repositoryUnits = mock(RepositoryUnits.class);
        repositoryUnitScore = mock(RepositoryUnitScore.class);
        repositoryAttendance = mock(RepositoryAttendance.class);
        repositoryCourseEnrollment = mock(RepositoryCourseEnrollment.class);
        repositoryStudent = mock(RepositoryStudent.class);
        repositoryGradeLog = mock(RepositoryGradeLog.class);

        businessGroupRegister = new BusinessGroupRegister(
                repositoryGroup,
                repositoryGroupStudent,
                repositoryUnits,
                repositoryUnitScore,
                repositoryAttendance,
                repositoryCourseEnrollment,
                repositoryStudent,
                repositoryGradeLog
        );
    }

    @Test
    void testGetGroupRegisterDataGroupNotFound() {
        when(repositoryGroup.findById("invalid")).thenReturn(Optional.empty());

        ResponseGroupRegisterData response = businessGroupRegister.getGroupRegisterData("invalid");

        assertFalse(response.isSuccess());
        assertTrue(response.getListMessage().get(0).contains("Error"));
    }

    @Test
    void testGetGroupRegisterDataSuccess() {
        EntityGroup group = new EntityGroup();
        group.setIdGroup("g1");
        group.setNameGroup("Grupo A");
        group.setConceptualWeight(30.0);
        group.setAttitudinalWeight(20.0);
        group.setPracticalWeight(50.0);

        EntityCourse course = new EntityCourse();
        course.setIdCourse("c1");
        course.setCode("CS101");
        course.setNameCourse("Curso 1");
        group.setParentCourse(course);

        EntityAcademicPeriod period = new EntityAcademicPeriod();
        period.setIdPeriod("p1");
        period.setYearPeriod(2026);
        period.setNumberPeriod(1);
        period.setStartDate(new java.sql.Date(System.currentTimeMillis()));
        period.setEndDate(new java.sql.Date(System.currentTimeMillis() + 864000000L));
        group.setParentAcademicperiod(period);

        EntityProfessor prof = new EntityProfessor();
        prof.setIdProfessor("prof1");
        EntityUser profUser = new EntityUser();
        profUser.setFirstName("Juan");
        profUser.setSurName("Perez");
        prof.setParentUser(profUser);
        group.setParentProfessor(prof);
        group.setChildSchedule(Collections.emptyList());

        when(repositoryGroup.findById("g1")).thenReturn(Optional.of(group));

        EntityUnits u1 = new EntityUnits();
        u1.setIdUnits("u1");
        u1.setNumberUnit(1);
        u1.setNameUnit("Unidad 1");

        when(repositoryUnits.findByParentGroupOrderByNumberUnitAsc(group)).thenReturn(List.of(u1));

        EntityStudent student = new EntityStudent();
        student.setIdStudent("st1");
        student.setCode("ST001");
        EntityUser user = new EntityUser();
        user.setFirstName("Maria");
        user.setSurName("Gomez");
        student.setParentUser(user);

        EntityGroupStudent gs = new EntityGroupStudent();
        gs.setIdGroupStudent("gs1");
        gs.setParentStudent(student);
        gs.setParentGroup(group);

        gs.setChildUnitscore(Collections.emptyList());

        when(repositoryGroupStudent.findByParentGroup(group)).thenReturn(new ArrayList<>(List.of(gs)));
        when(repositoryAttendance.findByParentGroupStudentIn(any())).thenReturn(Collections.emptyList());
        when(repositoryCourseEnrollment.findByStudentCourseAndPeriod(anyString(), anyString(), anyString())).thenReturn(Optional.empty());

        ResponseGroupRegisterData response = businessGroupRegister.getGroupRegisterData("g1");

        assertTrue(response.isSuccess());
        assertNotNull(response.getGroupInfo());
        assertEquals("g1", response.getGroupInfo().getIdGroup());
    }

    @Test
    void testSaveGroupRegisterDataGroupNotFound() {
        RequestGroupRegisterSave req = new RequestGroupRegisterSave();
        when(repositoryGroup.findById("invalid")).thenReturn(Optional.empty());

        Map<String, Object> result = businessGroupRegister.saveGroupRegisterData("invalid", req);
        assertFalse((Boolean) result.get("success"));
    }

    @Test
    void testSaveGroupRegisterDataSuccess() {
        EntityGroup group = new EntityGroup();
        group.setIdGroup("g1");

        EntityCourse course = new EntityCourse();
        course.setIdCourse("c1");
        group.setParentCourse(course);

        EntityAcademicPeriod period = new EntityAcademicPeriod();
        period.setIdPeriod("p1");
        group.setParentAcademicperiod(period);

        when(repositoryGroup.findById("g1")).thenReturn(Optional.of(group));

        EntityStudent student = new EntityStudent();
        student.setIdStudent("st1");
        student.setCode("ST001");

        EntityGroupStudent gs = new EntityGroupStudent();
        gs.setIdGroupStudent("gs1");
        gs.setParentStudent(student);
        gs.setParentGroup(group);

        when(repositoryGroupStudent.findByParentGroup(group)).thenReturn(List.of(gs));
        when(repositoryCourseEnrollment.findByStudentCourseAndPeriod("st1", "c1", "p1")).thenReturn(Optional.empty());

        RequestGroupRegisterSave req = new RequestGroupRegisterSave();
        RequestGroupRegisterSave.StudentSaveData ssd = new RequestGroupRegisterSave.StudentSaveData();
        ssd.setIdGroupStudent("gs1");
        ssd.setUnitScores(Collections.emptyList());
        ssd.setAttendances(Collections.emptyList());
        req.setStudents(List.of(ssd));

        Map<String, Object> result = businessGroupRegister.saveGroupRegisterData("g1", req);
        assertTrue((Boolean) result.get("success"));
    }

    @Test
    void testCloseGroupRegisterSuccess() {
        EntityGroup group = new EntityGroup();
        group.setIdGroup("g1");
        group.setConceptualWeight(0.4);
        group.setPracticalWeight(0.4);
        group.setAttitudinalWeight(0.2);

        EntityCourse course = new EntityCourse();
        course.setIdCourse("c1");
        group.setParentCourse(course);

        EntityAcademicPeriod period = new EntityAcademicPeriod();
        period.setIdPeriod("p1");
        group.setParentAcademicperiod(period);

        when(repositoryGroup.findById("g1")).thenReturn(Optional.of(group));

        EntityStudent student = new EntityStudent();
        student.setIdStudent("st1");
        student.setCode("ST001");

        EntityGroupStudent gs = new EntityGroupStudent();
        gs.setIdGroupStudent("gs1");
        gs.setParentStudent(student);

        EntityUnits unit = new EntityUnits();
        unit.setIdUnits("u1");
        unit.setNumberUnit(1);

        EntityUnitscore score = new EntityUnitscore();
        score.setParentUnits(unit);
        score.setConceptualScore(15.0);
        score.setPracticalScore(15.0);
        score.setAttitudinalScore(15.0);
        score.setScore(15.0);
        gs.setChildUnitscore(List.of(score));

        when(repositoryGroupStudent.findByParentGroup(group)).thenReturn(List.of(gs));
        when(repositoryUnits.findByParentGroupOrderByNumberUnitAsc(group)).thenReturn(List.of(unit));

        EntityCourseEnrollment officialEnrollment = new EntityCourseEnrollment();
        when(repositoryCourseEnrollment.findByStudentCourseAndPeriod("st1", "c1", "p1")).thenReturn(Optional.of(officialEnrollment));

        Map<String, Object> result = businessGroupRegister.closeGroupRegister("g1");
        assertTrue((Boolean) result.get("success"));
        verify(repositoryCourseEnrollment, times(1)).saveAll(anyList());
        verify(repositoryStudent, times(1)).saveAll(anyList());
    }

    @Test
    void testCloseGroupRegisterNoEnrollments() {
        EntityGroup group = new EntityGroup();
        group.setIdGroup("g1");
        when(repositoryGroup.findById("g1")).thenReturn(Optional.of(group));
        when(repositoryGroupStudent.findByParentGroup(group)).thenReturn(Collections.emptyList());

        Map<String, Object> result = businessGroupRegister.closeGroupRegister("g1");
        assertFalse((Boolean) result.get("success"));
        assertEquals("El grupo no tiene estudiantes inscritos.", result.get("error"));
    }

    @Test
    void testCloseGroupRegisterNoUnits() {
        EntityGroup group = new EntityGroup();
        group.setIdGroup("g1");
        EntityGroupStudent gs = new EntityGroupStudent();
        when(repositoryGroup.findById("g1")).thenReturn(Optional.of(group));
        when(repositoryGroupStudent.findByParentGroup(group)).thenReturn(List.of(gs));
        when(repositoryUnits.findByParentGroupOrderByNumberUnitAsc(group)).thenReturn(Collections.emptyList());

        Map<String, Object> result = businessGroupRegister.closeGroupRegister("g1");
        assertFalse((Boolean) result.get("success"));
        assertEquals("El curso no tiene unidades académicas configuradas.", result.get("error"));
    }

    @Test
    void testSaveGroupRegisterDataWithAttendancesAndScores() {
        EntityGroup group = new EntityGroup();
        group.setIdGroup("g1");
        EntityCourse course = new EntityCourse();
        course.setIdCourse("c1");
        group.setParentCourse(course);
        EntityAcademicPeriod period = new EntityAcademicPeriod();
        period.setIdPeriod("p1");
        group.setParentAcademicperiod(period);

        when(repositoryGroup.findById("g1")).thenReturn(Optional.of(group));

        EntityStudent student = new EntityStudent();
        student.setIdStudent("st1");
        EntityGroupStudent gs = new EntityGroupStudent();
        gs.setIdGroupStudent("gs1");
        gs.setParentStudent(student);

        when(repositoryGroupStudent.findByParentGroup(group)).thenReturn(List.of(gs));

        RequestGroupRegisterSave req = new RequestGroupRegisterSave();
        RequestGroupRegisterSave.StudentSaveData ssd = new RequestGroupRegisterSave.StudentSaveData();
        ssd.setIdGroupStudent("gs1");

        RequestGroupRegisterSave.UnitScoreSaveData ussd = new RequestGroupRegisterSave.UnitScoreSaveData();
        ussd.setIdUnits("u1");
        ussd.setConceptualScore(16.0);
        ussd.setPracticalScore(14.0);
        ussd.setTestGrades("15.0,17.0");
        ussd.setAttitudinalScore(18.0);
        ussd.setScore(16.0);
        ssd.setUnitScores(List.of(ussd));

        RequestGroupRegisterSave.AttendanceSaveData asd1 = new RequestGroupRegisterSave.AttendanceSaveData();
        asd1.setDate("01-08-2026");
        asd1.setStatus("P");

        RequestGroupRegisterSave.AttendanceSaveData asd2 = new RequestGroupRegisterSave.AttendanceSaveData();
        asd2.setIdAttendance("att1");
        asd2.setStatus("");

        ssd.setAttendances(List.of(asd1, asd2));
        req.setStudents(List.of(ssd));

        Map<String, Object> result = businessGroupRegister.saveGroupRegisterData("g1", req);
        assertTrue((Boolean) result.get("success"));
        verify(repositoryUnitScore, times(1)).save(any());
        verify(repositoryAttendance, times(1)).deleteById("att1");
        verify(repositoryAttendance, times(1)).save(any());
    }
}
