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

import com.epiis.finalproject.business.BusinessUnitScore;
import com.epiis.finalproject.dto.request.unitscore.RequestUnitScoreInsert;
import com.epiis.finalproject.dto.request.unitscore.RequestUnitScoreUpdate;
import com.epiis.finalproject.dto.response.unitscore.ResponseUnitScoreDeleteById;
import com.epiis.finalproject.dto.response.unitscore.ResponseUnitScoreInsert;
import com.epiis.finalproject.dto.response.unitscore.ResponseUnitScoreUpdate;

@PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
@RestController
@RequestMapping(path = "intranet")
public class UnitScoreController {
	private final BusinessUnitScore businessUnitScore;
	
	public UnitScoreController(BusinessUnitScore businessUnitScore) {
		this.businessUnitScore = businessUnitScore;
	}
	
	@PostMapping(path = "registerunitscore", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseUnitScoreInsert> insert(@Valid @RequestBody RequestUnitScoreInsert request){
		ResponseUnitScoreInsert response = businessUnitScore.insert(request);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(path = "indexunitscore")
	public ResponseEntity<Map<String, Object>> getAll(){
		
		return ResponseEntity.ok(businessUnitScore.getAll());
	}
	
	@GetMapping(path = "showunitscore/{idUnitScore}")
	public ResponseEntity<Map<String, Object>> getById(@PathVariable String idUnitScore){
		
		return ResponseEntity.ok(businessUnitScore.getById(idUnitScore));
	}
	
	@DeleteMapping(path = "deleteunitscore/{idUnitScore}")
	public ResponseEntity<ResponseUnitScoreDeleteById> deleteById(@PathVariable String idUnitScore){
		ResponseUnitScoreDeleteById response = businessUnitScore.deleteById(idUnitScore);
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping(path = "updateunitscore/{idUnitScore}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseUnitScoreUpdate> update(@PathVariable String idUnitScore, @Valid @RequestBody RequestUnitScoreUpdate request){
		ResponseUnitScoreUpdate response = businessUnitScore.update(idUnitScore, request);
		
		return ResponseEntity.ok(response);
	}
}
