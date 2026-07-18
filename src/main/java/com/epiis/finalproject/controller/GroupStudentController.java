package com.epiis.finalproject.controller;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

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
import org.springframework.web.bind.annotation.RestController;

import com.epiis.finalproject.business.BusinessGroupStudent;
import com.epiis.finalproject.dto.request.groupstudent.RequestGroupStudentUpdate;
import com.epiis.finalproject.dto.request.groupstudent.RequestGroupStundentInsert;
import com.epiis.finalproject.dto.response.group.ResponseGroupDeleteById;
import com.epiis.finalproject.dto.response.groupstudent.ResponseGroupStudentInsert;
import com.epiis.finalproject.dto.response.groupstudent.ResponseGroupStudentUpdate;

@PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
@RestController
@RequestMapping(path = "intranet")
public class GroupStudentController {
	private final BusinessGroupStudent businessGroupStudent;
	
	public GroupStudentController(BusinessGroupStudent businessGroupStudent) {
		this.businessGroupStudent = businessGroupStudent;
	}
	
	@PostMapping(path = "registergroupstudent", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseGroupStudentInsert> insert(@Valid @RequestBody RequestGroupStundentInsert request){
		ResponseGroupStudentInsert response = businessGroupStudent.insert(request);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(path = "indexgroupstudent")
	public ResponseEntity<Map<String, Object>> insert(){
		
		return ResponseEntity.ok(businessGroupStudent.getAll());
	}
	
	@GetMapping(path = "showgroupstudent/{idGroupStudent}")
	public ResponseEntity<Map<String, Object>> getById(@PathVariable String idGroupStudent){
		
		return ResponseEntity.ok(businessGroupStudent.getById(idGroupStudent));
	}
	
	@DeleteMapping(path = "deletegroupstudent/{idGroupStudent}")
	public ResponseEntity<ResponseGroupDeleteById> deleteById(@PathVariable String idGroupStudent){
		ResponseGroupDeleteById response = businessGroupStudent.deleteById(idGroupStudent);
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping(path = "updategroupstudent/{idGroupStudent}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseGroupStudentUpdate> update(@PathVariable String idGroupStudent, @Valid @RequestBody RequestGroupStudentUpdate request){
		ResponseGroupStudentUpdate response = businessGroupStudent.update(idGroupStudent, request);
		
		return ResponseEntity.ok(response);
	}
}
