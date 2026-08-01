package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.schedule.RequestScheduleInsert;
import com.epiis.finalproject.dto.response.schedule.*;
import com.epiis.finalproject.entity.EntityGroup;
import com.epiis.finalproject.entity.EntityProfessor;
import com.epiis.finalproject.entity.EntitySchedule;
import com.epiis.finalproject.entity.EntityUser;
import com.epiis.finalproject.repository.RepositoryGroup;
import com.epiis.finalproject.repository.RepositoryGroupStudent;
import com.epiis.finalproject.repository.RepositorySchedule;
import com.epiis.finalproject.repository.RepositoryUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
                repositorySchedule, repositoryGroupStudent, repositoryUser, repositoryGroup
        );
    }

    @Test
    void testInsertScheduleGroupNotFound() {
        RequestScheduleInsert req = new RequestScheduleInsert();
        req.setIdGroup("g1");
        req.setIdProfessor("p1");
        req.setSchedules(Collections.emptyList());

        when(repositoryGroup.findById("g1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> businessSchedule.insert(List.of(req)));
    }

    @Test
    void testDeleteById() {
        ResponseScheduleDeleteById response = businessSchedule.deleteById("sch1");
        assertEquals("success", response.getType());
        verify(repositorySchedule, times(1)).deleteById("sch1");
    }
}
