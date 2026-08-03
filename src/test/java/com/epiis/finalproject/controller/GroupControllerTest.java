package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessGroup;
import com.epiis.finalproject.dto.request.group.*;
import com.epiis.finalproject.dto.response.group.*;
import com.epiis.finalproject.dto.response.groupstudent.ResponseGroupStudentInsert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.mockito.Mockito.*;

class GroupControllerTest extends BaseControllerTest {

    private BusinessGroup businessGroup;
    private GroupController groupController;

    @BeforeEach
    void setUp() {
        businessGroup = mock(BusinessGroup.class);
        groupController = new GroupController(businessGroup);
    }

    @Test
    void testInsert() {
        RequestGroupInsert req = new RequestGroupInsert();
        when(businessGroup.insert(req)).thenReturn(new ResponseGroupInsert());
        assertOk(groupController.insert(req));
    }

    @Test
    void testGetAll() {
        when(businessGroup.getAll()).thenReturn(emptyMap());
        assertOk(groupController.getAll());
    }

    @Test
    void testGetById() {
        when(businessGroup.getById("g1")).thenReturn(emptyMap());
        assertOk(groupController.getById("g1"));
    }

    @Test
    void testDeleteById() {
        when(businessGroup.deleteById("g1")).thenReturn(new ResponseGroupDeleteById());
        assertOk(groupController.deleteById("g1"));
    }

    @Test
    void testUpdate() {
        RequestGroupUpdate req = new RequestGroupUpdate();
        when(businessGroup.update("g1", req)).thenReturn(new ResponseGroupUpdate());
        assertOk(groupController.update("g1", req));
    }

    @Test
    void testAssignGroups() {
        RequestGroupAssignment req = new RequestGroupAssignment();
        when(businessGroup.createAndAssignGroups(req)).thenReturn(new ResponseGroupStudentInsert());
        assertOk(groupController.assignGroups(req));
    }

    @Test
    void testGetUnassignedStudents() {
        when(businessGroup.getUnassignedStudents("c1")).thenReturn(Collections.emptyList());
        assertOk(groupController.getUnassignedStudents("c1"));
    }
}
