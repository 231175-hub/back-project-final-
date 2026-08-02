package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.schedule.RequestScheduleInsert;
import com.epiis.finalproject.dto.request.schedule.RequestScheduleUpdate;
import com.epiis.finalproject.dto.response.schedule.*;
import com.epiis.finalproject.entity.*;
import com.epiis.finalproject.repository.RepositoryGroup;
import com.epiis.finalproject.repository.RepositoryGroupStudent;
import com.epiis.finalproject.repository.RepositorySchedule;
import com.epiis.finalproject.repository.RepositoryUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BusinessScheduleTest {

    private RepositorySchedule repositorySchedule;
    private RepositoryGroupStudent repositoryGroupStudent;
    private RepositoryUser repositoryUser;
    private RepositoryGroup repositoryGroup;
    private BusinessSchedule businessSchedule;

    @BeforeEach
    void setUp() {
        repositorySchedule = mock(RepositorySchedule.class);
        repositoryGroupStudent = mock(RepositoryGroupStudent.class);
        repositoryUser = mock(RepositoryUser.class);
        repositoryGroup = mock(RepositoryGroup.class);

        businessSchedule = new BusinessSchedule(
                repositorySchedule, repositoryGroupStudent, repositoryUser, repositoryGroup);
    }

    @Test
    void testInsertScheduleGroupNotFound() {
        RequestScheduleInsert req = new RequestScheduleInsert();
        req.setIdGroup("g1");
        req.setIdProfessor("p1");
        req.setSchedules(Collections.emptyList());

        when(repositoryGroup.findById("g1")).thenReturn(Optional.empty());

        List<RequestScheduleInsert> list = List.of(req);
        assertThrows(IllegalArgumentException.class, () -> businessSchedule.insert(list));
    }

    @Test
    void testInsertScheduleSuccessWithProfessorAndSchedules() {
        RequestScheduleInsert req = new RequestScheduleInsert();
        req.setIdGroup("g1");
        req.setIdProfessor("profUser1");

        EntitySchedule sch1 = new EntitySchedule();
        sch1.setDayWeek("Lunes");
        req.setSchedules(List.of(sch1));

        EntityGroup group = new EntityGroup();
        group.setIdGroup("g1");

        EntityUser user = new EntityUser();
        user.setIdUser("profUser1");
        EntityProfessor professor = new EntityProfessor();
        professor.setIdProfessor("prof1");
        user.setChildProfessor(professor);

        when(repositoryGroup.findById("g1")).thenReturn(Optional.of(group));
        when(repositoryUser.findById("profUser1")).thenReturn(Optional.of(user));

        ResponseScheduleInsert response = businessSchedule.insert(List.of(req));
        assertEquals("success", response.getType());
        verify(repositoryGroup, times(1)).save(group);
        verify(repositorySchedule, times(1)).saveAll(anyList());
    }

    @Test
    void testInsertScheduleProfessorUserNotFoundOrNotProfessor() {
        RequestScheduleInsert req = new RequestScheduleInsert();
        req.setIdGroup("g1");
        req.setIdProfessor("unknown");

        EntityGroup group = new EntityGroup();
        when(repositoryGroup.findById("g1")).thenReturn(Optional.of(group));
        when(repositoryUser.findById("unknown")).thenReturn(Optional.empty());

        List<RequestScheduleInsert> list1 = List.of(req);
        assertThrows(IllegalArgumentException.class, () -> businessSchedule.insert(list1));

        EntityUser notProfUser = new EntityUser();
        when(repositoryUser.findById("notProf")).thenReturn(Optional.of(notProfUser));
        req.setIdProfessor("notProf");

        List<RequestScheduleInsert> list2 = List.of(req);
        assertThrows(IllegalStateException.class, () -> businessSchedule.insert(list2));
    }

    @Test
    void testGetByIdAndGetAll() {
        when(repositorySchedule.findById("sch1")).thenReturn(Optional.of(new EntitySchedule()));
        Map<String, Object> byId = businessSchedule.getById("sch1");
        assertNotNull(byId.get("data"));
    }

    @Test
    void testDeleteById() {
        ResponseScheduleDeleteById response = businessSchedule.deleteById("sch1");
        assertEquals("success", response.getType());
        verify(repositorySchedule, times(1)).deleteById("sch1");
    }

    @Test
    void testUpdateSchedule() {
        EntitySchedule schedule = new EntitySchedule();
        schedule.setIdSchedule("sch1");
        when(repositorySchedule.findById("sch1")).thenReturn(Optional.of(schedule));

        RequestScheduleUpdate req = new RequestScheduleUpdate();
        req.setIdGroup("g1");
        req.setDayWeek("Martes");

        ResponseScheduleUpdate response = businessSchedule.update("sch1", req);
        assertEquals("success", response.getType());
        verify(repositorySchedule, times(1)).save(schedule);

        when(repositorySchedule.findById("notFound")).thenReturn(Optional.empty());
        ResponseScheduleUpdate responseError = businessSchedule.update("notFound", req);
        assertEquals("error", responseError.getType());
    }

    @Test
    void testGetStudentScheduleByUserEmail() {
        assertTrue(businessSchedule.getStudentScheduleByUserEmail(null).isEmpty());
        assertTrue(businessSchedule.getStudentScheduleByUserEmail("   ").isEmpty());

        EntityUser user = new EntityUser();
        EntityStudent student = new EntityStudent();
        student.setIdStudent("st1");
        user.setChildStudent(student);

        when(repositoryUser.findFirstByEmail("student@test.com")).thenReturn(Optional.of(user));
        when(repositoryGroupStudent.findCustomScheduleByStudentId("st1")).thenReturn(Collections.emptyList());

        List<ResponseScheduleGetAll> res = businessSchedule.getStudentScheduleByUserEmail("student@test.com");
        assertNotNull(res);
    }
}
