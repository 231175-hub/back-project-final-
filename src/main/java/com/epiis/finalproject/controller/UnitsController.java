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

import com.epiis.finalproject.business.BusinessUnits;
import com.epiis.finalproject.dto.request.units.RequestUnitsInsert;
import com.epiis.finalproject.dto.request.units.RequestUnitsUpdate;
import com.epiis.finalproject.dto.response.units.ResponseUnitsDeleteById;
import com.epiis.finalproject.dto.response.units.ResponseUnitsInsert;
import com.epiis.finalproject.dto.response.units.ResponseUnitsUpdate;

@PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
@RestController
@RequestMapping(path = "intranet")
public class UnitsController {
	private final BusinessUnits businessUnits;
	
	public UnitsController(BusinessUnits businessUnits) {
		this.businessUnits = businessUnits;
	}
	
	@PostMapping(path = "registerunits", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseUnitsInsert> insert(@Valid @RequestBody RequestUnitsInsert request){
		ResponseUnitsInsert response = businessUnits.insert(request);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(path = "indexunits")
	public ResponseEntity<Map<String, Object>> getAll(){
		
		return ResponseEntity.ok(businessUnits.getAll());
	}
	
	@GetMapping(path =  "showunits/{idUnits}")
	public ResponseEntity<Map<String, Object>> getById(@PathVariable String idUnits){
		
		return ResponseEntity.ok(businessUnits.getById(idUnits));
	}
	
	@DeleteMapping(path = "deleteunits/{idUnits}")
	public ResponseEntity<ResponseUnitsDeleteById> deleteById(@PathVariable String idUnits){
		ResponseUnitsDeleteById response = businessUnits.deleteById(idUnits);
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping(path = "updateunits/{idUnits}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseUnitsUpdate> insert(@PathVariable String idUnits, @Valid @RequestBody RequestUnitsUpdate request){
		ResponseUnitsUpdate response = businessUnits.update(idUnits, request);
		
		return ResponseEntity.ok(response);
	}
}
