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

import com.epiis.finalproject.business.BusinessStudent;
import com.epiis.finalproject.dto.request.student.RequestStudentInsert;
import com.epiis.finalproject.dto.request.student.RequestStudentUpdate;
import com.epiis.finalproject.dto.request.user.RequestUserUpdatePassword;
import com.epiis.finalproject.dto.response.student.ResponseStudentDeleteById;
import com.epiis.finalproject.dto.response.student.ResponseStudentInsert;
import com.epiis.finalproject.dto.response.student.ResponseStudentSearch;
import com.epiis.finalproject.dto.response.student.ResponseStudentUpdate;
import com.epiis.finalproject.dto.response.user.ResponseUserUpdatePassword;

import jakarta.validation.Valid;

@PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
@RestController
@RequestMapping(path = "intranet")
public class StudentController {
	private final BusinessStudent businessStudent;
	
	public StudentController(BusinessStudent businessStudent) {
		this.businessStudent = businessStudent;
	}
	
	@PostMapping(path = "registerstudent", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseStudentInsert> insert(@Valid @RequestBody RequestStudentInsert request){
		ResponseStudentInsert response = businessStudent.insert(request);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(path = "indexstudent")
	public ResponseEntity<Map<String, Object>> getAll(){
		
		return ResponseEntity.ok(businessStudent.getAll());
	}
	
	@GetMapping(path = "showstudent/{idStudent}")
	public ResponseEntity<Map<String, Object>> getById(@PathVariable String idStudent){
		
		return ResponseEntity.ok(businessStudent.getById(idStudent));
	}
	
	@DeleteMapping(path = "deletestudent/{idStudent}")
	public ResponseEntity<ResponseStudentDeleteById> deleteById(@PathVariable String idStudent){
		ResponseStudentDeleteById response = businessStudent.deleteById(idStudent);
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping(path = "updatestudent/{idStudent}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseStudentUpdate> update(@PathVariable String idStudent, @Valid @RequestBody RequestStudentUpdate request){
		ResponseStudentUpdate response = businessStudent.update(idStudent, request);
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping(path = "updatepasswordstudent/{email}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseUserUpdatePassword> updatePassword(@PathVariable String email ,@Valid @RequestBody RequestUserUpdatePassword request){
		ResponseUserUpdatePassword response = businessStudent.updatePassword(email, request);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/searchStudent")
	public ResponseEntity<List<ResponseStudentSearch>> searchStudent(@RequestParam("query") String query, @RequestParam("idSchool") String idSchool) {
		List<ResponseStudentSearch> result = businessStudent.searchStudentsForAutocomplete(query, idSchool);
		
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/report/{idStudentKeycloak}")
    public ResponseEntity<Map<String, Object>> getBoletasDeEstudiante(@PathVariable String idStudentKeycloak) {
        Map<String, Object> response = businessStudent.getAllBoletasForStudent(idStudentKeycloak);
        return ResponseEntity.ok(response);
    }
}
