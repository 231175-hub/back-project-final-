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

import com.epiis.finalproject.business.BusinessAcademicPeriod;
import com.epiis.finalproject.dto.request.academicperiod.RequestAcademicPeriodInsert;
import com.epiis.finalproject.dto.request.academicperiod.RequestAcademicPeriodUpdate;
import com.epiis.finalproject.dto.response.academicperiod.ResponseAcademicPeriodDeleteById;
import com.epiis.finalproject.dto.response.academicperiod.ResponseAcademicPeriodInsert;
import com.epiis.finalproject.dto.response.academicperiod.ResponseAcademicPeriodUpdate;

@RestController
@RequestMapping(path = "intranet")
public class AcademicPeriodController {
	private final BusinessAcademicPeriod businessAcademicPeriod;
	
	private static final String STR_ERROR = "error";

	public AcademicPeriodController(BusinessAcademicPeriod businessAcademicPeriod) {
		this.businessAcademicPeriod = businessAcademicPeriod;
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR')")
	@PostMapping(path = "registeracademicperiod", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseAcademicPeriodInsert> insert(@Valid @RequestBody RequestAcademicPeriodInsert request){
		ResponseAcademicPeriodInsert response = businessAcademicPeriod.insert(request);
		if (STR_ERROR.equals(response.getType())) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR', 'PROFESSOR', 'PROFESOR', 'STUDENT', 'ESTUDIANTE')")
	@GetMapping(path = "indexacademicperiod")
	public ResponseEntity<Map<String, Object>> getAll(){
		
		return ResponseEntity.ok(businessAcademicPeriod.getAll());
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR', 'PROFESSOR', 'PROFESOR', 'STUDENT', 'ESTUDIANTE')")
	@GetMapping(path = "showacademicperiod/{idPeriod}")
	public ResponseEntity<Map<String, Object>> getById(@PathVariable String idPeriod){
		
		return ResponseEntity.ok(businessAcademicPeriod.getById(idPeriod));
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR')")
	@DeleteMapping(path = "deleteacademicperiod/{idPeriod}")
	public ResponseEntity<ResponseAcademicPeriodDeleteById> deleteById(@PathVariable String idPeriod){
		ResponseAcademicPeriodDeleteById response = businessAcademicPeriod.deleteById(idPeriod);
		
		return ResponseEntity.ok(response);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR', 'PROFESSOR', 'PROFESOR', 'STUDENT', 'ESTUDIANTE')")
	@GetMapping(path = "academicperiod/statuses")
	public ResponseEntity<Object> getStatuses(){
		try {
			java.util.List<String> list = java.util.Arrays.stream(com.epiis.finalproject.staticdata.EnumAcademicPeriod.values())
				.map(com.epiis.finalproject.staticdata.EnumAcademicPeriod::toString)
				.filter(status -> "Activo".equals(status) || "Planificado".equals(status))
				.toList();
			return ResponseEntity.ok(list);
		} catch (Exception t) {
			t.printStackTrace();
			java.util.Map<String, String> err = new java.util.HashMap<>();
			err.put(STR_ERROR, t.getClass().getName());
			err.put("message", t.getMessage());
			return ResponseEntity.status(500).body(err);
		}
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR')")
	@PutMapping(path = "updateacademicperiod/{idPeriod}")
	public ResponseEntity<ResponseAcademicPeriodUpdate> update(@PathVariable String idPeriod, @Valid @RequestBody RequestAcademicPeriodUpdate request){
		ResponseAcademicPeriodUpdate response = businessAcademicPeriod.update(idPeriod, request);
		if (STR_ERROR.equals(response.getType())) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
}
