package com.epiis.finalproject.controller;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epiis.finalproject.business.BusinessGroup;
import com.epiis.finalproject.dto.request.group.RequestGroupAssignment;
import com.epiis.finalproject.dto.request.group.RequestGroupInsert;
import com.epiis.finalproject.dto.request.group.RequestGroupUpdate;
import com.epiis.finalproject.dto.response.group.ResponseGroupDeleteById;
import com.epiis.finalproject.dto.response.group.ResponseGroupInsert;
import com.epiis.finalproject.dto.response.group.ResponseGroupUpdate;
import com.epiis.finalproject.dto.response.group.ResponseProfessorGroups;
import com.epiis.finalproject.dto.response.groupstudent.ResponseGroupStudentInsert;
import com.epiis.finalproject.dto.response.student.ResponseUnassignedStudent;
import com.epiis.finalproject.entity.EntityGroup;

@RestController
@RequestMapping(path = "intranet")
public class GroupController {
	private final BusinessGroup businessGroup;
	
	public GroupController(BusinessGroup businessGroup) {
		this.businessGroup = businessGroup;
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR')")
	@PostMapping(path = "registergroup", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseGroupInsert> insert(@Valid @RequestBody RequestGroupInsert request){
		ResponseGroupInsert response = businessGroup.insert(request);
		
		return ResponseEntity.ok(response);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR', 'PROFESSOR', 'PROFESOR')")
	@GetMapping(path = "indexgroup")
	public ResponseEntity<Map<String, Object>> getAll(){
		
		return ResponseEntity.ok(businessGroup.getAll());
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR', 'PROFESSOR', 'PROFESOR')")
	@GetMapping(path = "showgroup/{idGroup}")
	public ResponseEntity<Map<String, Object>> getById(@PathVariable String idGroup){
		
		return ResponseEntity.ok(businessGroup.getById(idGroup));
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR')")
	@DeleteMapping(path = "deletegroup/{idGroup}")
	public ResponseEntity<ResponseGroupDeleteById> deleteById(@PathVariable String idGroup){
		ResponseGroupDeleteById response = businessGroup.deleteById(idGroup);
		
		return ResponseEntity.ok(response);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR')")
	@PutMapping(path = "updategroup/{idGroup}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseGroupUpdate> update(@PathVariable String idGroup, @Valid @RequestBody RequestGroupUpdate request){
		ResponseGroupUpdate response = businessGroup.update(idGroup, request);
		
		return ResponseEntity.ok(response);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR')")
	@PostMapping("/assignGroups")
    public ResponseEntity<ResponseGroupStudentInsert> assignGroups(@Valid @RequestBody RequestGroupAssignment request) {
        ResponseGroupStudentInsert response = businessGroup.createAndAssignGroups(request);
        return ResponseEntity.ok(response);
    }
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR', 'PROFESSOR', 'PROFESOR')")
	@GetMapping("/getUnassignedStudents")
    public ResponseEntity<List<ResponseUnassignedStudent>> getUnassignedStudents(@RequestParam("idCourse") String idCourse) {
        List<ResponseUnassignedStudent> result = businessGroup.getUnassignedStudents(idCourse);
        return ResponseEntity.ok(result);
    }
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR', 'PROFESSOR', 'PROFESOR')")
	@GetMapping("getGroupWithCourse/{idCourse}")
    public ResponseEntity<?> getGroupsByCourseId(@PathVariable String idCourse) {
        List<EntityGroup> groups = businessGroup.getGroupsByCourse(idCourse);
        return ResponseEntity.ok(Map.of("data", groups));
    }
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR', 'PROFESSOR', 'PROFESOR')")
	@GetMapping("/professorgroups/{idProfessor}")
    public ResponseEntity<ResponseProfessorGroups> getGroupsByProfessor(@PathVariable String idProfessor) {
        ResponseProfessorGroups response = businessGroup.getGroupsByProfessor(idProfessor);
        return ResponseEntity.ok(response);
    }

	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR', 'PROFESSOR', 'PROFESOR')")
	@PutMapping(path = "updategroupweights/{idGroup}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseGroupUpdate> updateWeights(@PathVariable String idGroup, @RequestBody Map<String, Double> weights) {
		double conceptual = weights.getOrDefault("conceptualWeight", 0.0);
		double practical = weights.getOrDefault("practicalWeight", 0.0);
		double attitudinal = weights.getOrDefault("attitudinalWeight", 0.0);
		ResponseGroupUpdate response = businessGroup.updateWeights(idGroup, conceptual, practical, attitudinal);
		return ResponseEntity.ok(response);
	}
}
