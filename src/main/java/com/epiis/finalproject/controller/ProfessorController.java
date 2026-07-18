package com.epiis.finalproject.controller;
import org.springframework.security.access.prepost.PreAuthorize;

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

import com.epiis.finalproject.business.BusinessProfessor;
import com.epiis.finalproject.dto.request.professor.RequestProfessorInsert;
import com.epiis.finalproject.dto.request.professor.RequestProfessorUpdate;
import com.epiis.finalproject.dto.request.user.RequestUserUpdatePassword;
import com.epiis.finalproject.dto.response.professor.ResponseProfessorDeleteById;
import com.epiis.finalproject.dto.response.professor.ResponseProfessorInsert;
import com.epiis.finalproject.dto.response.professor.ResponseProfessorUpdate;
import com.epiis.finalproject.dto.response.user.ResponseUserUpdatePassword;

import jakarta.validation.Valid;

@PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
@RestController
@RequestMapping(path = "intranet")
public class ProfessorController {
	private final BusinessProfessor businessProfessor;
	
	public ProfessorController(BusinessProfessor businessProfessor) {
		this.businessProfessor = businessProfessor;
	}
	
	@PostMapping(path = "registerprofessor", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseProfessorInsert> insert(@Valid @RequestBody RequestProfessorInsert request){
		ResponseProfessorInsert response = businessProfessor.insert(request);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(path = "indexprofessor")
	public ResponseEntity<Map<String, Object>> getAll(){
		
		return ResponseEntity.ok(businessProfessor.getAll());
	}
	
	@GetMapping(path = "showprofessor/{idProfessor}")
	public ResponseEntity<Map<String, Object>> getById(@PathVariable String idProfessor){
		
		return ResponseEntity.ok(businessProfessor.getById(idProfessor));
	}
	
	@DeleteMapping(path = "deleteprofessor/{idProfessor}")
	public ResponseEntity<ResponseProfessorDeleteById> deleteById(@PathVariable String idProfessor){
		ResponseProfessorDeleteById response = businessProfessor.deleteById(idProfessor);
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping(path = "updateprofessor/{idProfessor}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseProfessorUpdate> update(@PathVariable String idProfessor, @Valid @RequestBody RequestProfessorUpdate request){
		ResponseProfessorUpdate response = businessProfessor.update(idProfessor, request);
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping(path = "updatepasswordprofessor/{email}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseUserUpdatePassword> updatePassword(@PathVariable String email ,@Valid @RequestBody RequestUserUpdatePassword request){
		ResponseUserUpdatePassword response = businessProfessor.updatePassword(email, request);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("searchprofessor")
	public ResponseEntity<List<Map<String, Object>>> searchProfessor(@RequestParam String query) {
	    return ResponseEntity.ok(businessProfessor.searchProfessorsForAutocomplete(query));
	}
}
