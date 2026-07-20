package com.epiis.finalproject.controller;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

import java.security.Principal;
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
import org.springframework.web.bind.annotation.RestController;

import com.epiis.finalproject.business.BusinessSchedule;
import com.epiis.finalproject.dto.request.schedule.RequestScheduleInsert;
import com.epiis.finalproject.dto.request.schedule.RequestScheduleUpdate;
import com.epiis.finalproject.dto.response.schedule.ResponseScheduleDeleteById;
import com.epiis.finalproject.dto.response.schedule.ResponseScheduleInsert;
import com.epiis.finalproject.dto.response.schedule.ResponseScheduleUpdate;
import com.epiis.finalproject.dto.response.schedule.ResponseScheduleGetAll;

@RestController
@RequestMapping(path = "intranet")
public class ScheduleController {
	private final BusinessSchedule businessSchedule;
	
	public ScheduleController(BusinessSchedule businessSchedule) {
		this.businessSchedule = businessSchedule;
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR')")
	@PostMapping(path = "registerschedule", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseScheduleInsert> insert(@Valid @RequestBody List<RequestScheduleInsert> request){
		ResponseScheduleInsert response = businessSchedule.insert(request);
		
		return ResponseEntity.ok(response);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR', 'STUDENT', 'ESTUDIANTE', 'PROFESSOR', 'PROFESOR')")
	@GetMapping(path = "indexschedule")
	public ResponseEntity<List<ResponseScheduleGetAll>> getAll(Principal principal){
		String email = principal != null ? principal.getName() : null;
		return ResponseEntity.ok(businessSchedule.getStudentScheduleByUserEmail(email));
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR', 'STUDENT', 'ESTUDIANTE', 'PROFESSOR', 'PROFESOR')")
	@GetMapping(path = "showschedule/{idSchedule}")
	public ResponseEntity<Map<String, Object>> getById(@PathVariable String idSchedule){
		
		return ResponseEntity.ok(businessSchedule.getById(idSchedule));
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR')")
	@DeleteMapping(path = "deleteschedule/{idSchedule}")
	public ResponseEntity<ResponseScheduleDeleteById> deleteById(@PathVariable String idSchedule){
		ResponseScheduleDeleteById response = businessSchedule.deleteById(idSchedule);
		
		return ResponseEntity.ok(response);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR')")
	@PutMapping(path = "updateschedule/{idSchedule}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseScheduleUpdate> update(@PathVariable String idSchedule, @Valid @RequestBody RequestScheduleUpdate request){
		ResponseScheduleUpdate response = businessSchedule.update(idSchedule, request);
		
		return ResponseEntity.ok(response);
	}
}
