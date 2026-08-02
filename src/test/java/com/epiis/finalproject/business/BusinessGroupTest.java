package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.group.RequestGroupAssignment;
import com.epiis.finalproject.dto.request.group.RequestGroupInsert;
import com.epiis.finalproject.dto.request.group.RequestGroupUpdate;
import com.epiis.finalproject.dto.response.group.*;
import com.epiis.finalproject.dto.response.groupstudent.ResponseGroupStudentInsert;
import com.epiis.finalproject.entity.*;
import com.epiis.finalproject.repository.*;
import com.epiis.finalproject.staticdata.EnumAcademicPeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class BusinessGroupTest {

    private RepositoryGroup repositoryGroup;
    private RepositoryGroupStudent repositoryGroupStudent;
    private RepositoryCourseEnrollment repositoryCourseEnrollment;
    private RepositoryAcademicperiod repositoryAcademicperiod;
    private RepositoryCourse repositoryCourse;
    private RepositoryUnits repositoryUnits;

    private BusinessGroup businessGroup;

    @BeforeEach
    void setUp() {
        repositoryGroup = mock(RepositoryGroup.class);
        repositoryGroupStudent = mock(RepositoryGroupStudent.class);
        repositoryCourseEnrollment = mock(RepositoryCourseEnrollment.class);
        repositoryAcademicperiod = mock(RepositoryAcademicperiod.class);
        repositoryCourse = mock(RepositoryCourse.class);
        repositoryUnits = mock(RepositoryUnits.class);

        businessGroup = new BusinessGroup(
                repositoryGroup, repositoryGroupStudent, repositoryCourseEnrollment,
                repositoryAcademicperiod, repositoryCourse, repositoryUnits
        );
    }

    @Test
    void testInsert() {
        RequestGroupInsert req = new RequestGroupInsert();
        req.setNameGroup("Grupo A");
        req.setIdPeriod("p1");
        req.setIdProfessor("prof1");
        req.setIdCourse("c1");

        ResponseGroupInsert response = businessGroup.insert(req);
        assertEquals("success", response.getType());
        verify(repositoryGroup, times(1)).save(any());
        verify(repositoryUnits, times(3)).save(any());
    }

    @Test
    void testGetAllAndGetById() {
        when(repositoryGroup.findAll()).thenReturn(Collections.emptyList());
        when(repositoryGroup.findById("g1")).thenReturn(Optional.of(new EntityGroup()));

        Map<String, Object> all = businessGroup.getAll();
        assertNotNull(all.get("data"));

        Map<String, Object> byId = businessGroup.getById("g1");
        assertNotNull(byId.get("data"));
    }

    @Test
    void testDeleteById() {
        ResponseGroupDeleteById response = businessGroup.deleteById("g1");
        assertEquals("success", response.getType());
        verify(repositoryGroup, times(1)).deleteById("g1");
    }

    @Test
    void testUpdate() {
        when(repositoryGroup.findById("g1")).thenReturn(Optional.of(new EntityGroup()));

        RequestGroupUpdate req = new RequestGroupUpdate();
        req.setNameGroup("Grupo B");
        ResponseGroupUpdate response = businessGroup.update("g1", req);
        assertEquals("success", response.getType());

        when(repositoryGroup.findById("notFound")).thenReturn(Optional.empty());
        ResponseGroupUpdate responseError = businessGroup.update("notFound", req);
        assertEquals("error", responseError.getType());
    }

    @Test
    void testCreateAndAssignGroupsNoActivePeriod() {
        RequestGroupAssignment req = new RequestGroupAssignment();
        req.setIdCourse("c1");
        when(repositoryAcademicperiod.findByStatus(EnumAcademicPeriod.ACTIVE.toString())).thenReturn(Optional.empty());

        ResponseGroupStudentInsert response = businessGroup.createAndAssignGroups(req);
        assertEquals("error", response.getType());
    }

    @Test
    void testCreateAndAssignGroupsSuccess() {
        RequestGroupAssignment req = new RequestGroupAssignment();
        req.setIdCourse("c1");

        EntityAcademicPeriod activePeriod = new EntityAcademicPeriod();
        activePeriod.setIdPeriod("p1");
        when(repositoryAcademicperiod.findByStatus(EnumAcademicPeriod.ACTIVE.toString())).thenReturn(Optional.of(activePeriod));

        EntityCourseEnrollment enrollment = new EntityCourseEnrollment();
        enrollment.setParentStudent(new EntityStudent());
        when(repositoryCourseEnrollment.findUnassignedStudents("c1", "p1")).thenReturn(List.of(enrollment));

        EntityCourse course = new EntityCourse();
        when(repositoryCourse.getReferenceById("c1")).thenReturn(course);
        when(repositoryGroup.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        ResponseGroupStudentInsert response = businessGroup.createAndAssignGroups(req);
        assertEquals("success", response.getType());
    }

    @Test
    void testGetGroupsByCourseAndProfessor() {
        when(repositoryGroup.findAllGroupsByCourse("c1")).thenReturn(Collections.emptyList());
        List<EntityGroup> groups = businessGroup.getGroupsByCourse("c1");
        assertNotNull(groups);

        when(repositoryGroup.findGroupsByProfessor("prof1")).thenReturn(Collections.emptyList());
        ResponseProfessorGroups profGroups = businessGroup.getGroupsByProfessor("prof1");
        assertEquals("success", profGroups.getType());
    }

    @Test
    void testUpdateWeights() {
        EntityGroup group = new EntityGroup();
        group.setIdGroup("g1");
        when(repositoryGroup.findById("g1")).thenReturn(Optional.of(group));

        ResponseGroupUpdate response = businessGroup.updateWeights("g1", 0.4, 0.4, 0.2);
        assertEquals("success", response.getType());
    }
}
