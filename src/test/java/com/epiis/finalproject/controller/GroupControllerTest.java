package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessGroup;
import com.epiis.finalproject.dto.request.group.*;
import com.epiis.finalproject.dto.response.group.*;
import com.epiis.finalproject.dto.response.groupstudent.ResponseGroupStudentInsert;
import com.epiis.finalproject.dto.response.student.ResponseUnassignedStudent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupControllerTest {

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
        ResponseGroupInsert resp = new ResponseGroupInsert();
        when(businessGroup.insert(req)).thenReturn(resp);

        ResponseEntity<ResponseGroupInsert> result = groupController.insert(req);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetAll() {
        Map<String, Object> map = new HashMap<>();
        when(businessGroup.getAll()).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = groupController.getAll();
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetById() {
        Map<String, Object> map = new HashMap<>();
        when(businessGroup.getById("g1")).thenReturn(map);

        ResponseEntity<Map<String, Object>> result = groupController.getById("g1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testDeleteById() {
        ResponseGroupDeleteById resp = new ResponseGroupDeleteById();
        when(businessGroup.deleteById("g1")).thenReturn(resp);

        ResponseEntity<ResponseGroupDeleteById> result = groupController.deleteById("g1");
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testUpdate() {
        RequestGroupUpdate req = new RequestGroupUpdate();
        ResponseGroupUpdate resp = new ResponseGroupUpdate();
        when(businessGroup.update("g1", req)).thenReturn(resp);

        ResponseEntity<ResponseGroupUpdate> result = groupController.update("g1", req);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testAssignGroups() {
        RequestGroupAssignment req = new RequestGroupAssignment();
        ResponseGroupStudentInsert resp = new ResponseGroupStudentInsert();
        when(businessGroup.createAndAssignGroups(req)).thenReturn(resp);

        ResponseEntity<ResponseGroupStudentInsert> result = groupController.assignGroups(req);
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void testGetUnassignedStudents() {
        List<ResponseUnassignedStudent> list = Collections.emptyList();
        when(businessGroup.getUnassignedStudents("c1")).thenReturn(list);

        ResponseEntity<List<ResponseUnassignedStudent>> result = groupController.getUnassignedStudents("c1");
        assertEquals(200, result.getStatusCode().value());
    }
}
