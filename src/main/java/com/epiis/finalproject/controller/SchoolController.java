package com.epiis.finalproject.controller;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epiis.finalproject.business.BusinessSchool;
import com.epiis.finalproject.dto.request.school.RequestSchoolInsert;
import com.epiis.finalproject.dto.request.school.RequestSchoolUpdate;
import com.epiis.finalproject.dto.response.school.ResponseSchoolDeleteById;
import com.epiis.finalproject.dto.response.school.ResponseSchoolInsert;
import com.epiis.finalproject.dto.response.school.ResponseSchoolUpdate;


@RestController
@RequestMapping(path = "intranet")
public class SchoolController {
	private final BusinessSchool businessSchool;
	
	public SchoolController(BusinessSchool businessSchool) {
		this.businessSchool = businessSchool;
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR')")
	@PostMapping(path = "registerschool", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ResponseSchoolInsert> actionInsert(@ModelAttribute RequestSchoolInsert request) throws Exception{
		ResponseSchoolInsert response = businessSchool.insert(request);
		
		return ResponseEntity.ok(response);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR', 'PROFESSOR', 'PROFESOR', 'STUDENT', 'ESTUDIANTE')")
	@GetMapping(path = "indexschool")
	public ResponseEntity<Map<String, Object>> getAll(){
		
		return ResponseEntity.ok(businessSchool.getAll());
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR', 'PROFESSOR', 'PROFESOR', 'STUDENT', 'ESTUDIANTE')")
	@GetMapping(path = "showschool/{idSchool}")
	public ResponseEntity<Map<String, Object>> getById(@PathVariable String idSchool){
		
		return ResponseEntity.ok(businessSchool.getById(idSchool));
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR')")
	@DeleteMapping(path = "deleteschool/{idSchool}")
	public ResponseEntity<ResponseSchoolDeleteById> deleteById(@PathVariable String idSchool) throws Exception{
		ResponseSchoolDeleteById response = businessSchool.deleteById(idSchool);
		
		return ResponseEntity.ok(response);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR')")
	@PutMapping(path = "updateschool/{idSchool}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ResponseSchoolUpdate> update(@PathVariable String idSchool, @ModelAttribute RequestSchoolUpdate request){
		ResponseSchoolUpdate response = businessSchool.update(idSchool, request);
		
		return ResponseEntity.ok(response);
	}
}
