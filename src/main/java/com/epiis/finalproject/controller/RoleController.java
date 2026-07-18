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

import com.epiis.finalproject.business.BusinessRole;
import com.epiis.finalproject.dto.request.role.RequestRoleInsert;
import com.epiis.finalproject.dto.request.role.RequestRoleUpdate;
import com.epiis.finalproject.dto.response.role.ResponseRoleDeleteById;
import com.epiis.finalproject.dto.response.role.ResponseRoleInsert;
import com.epiis.finalproject.dto.response.role.ResponseRoleUpdate;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping(path = "intranet")
public class RoleController {
	private final BusinessRole businessRole;
	
	public RoleController(BusinessRole businessRole) {
		this.businessRole = businessRole;
	}
	
	@PostMapping(path = "registerrole", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseRoleInsert> insert(@Valid @RequestBody RequestRoleInsert request){
		ResponseRoleInsert response = businessRole.insert(request);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(path = "indexrole")
	public ResponseEntity<Map<String, Object>> getAll(){
		
		return ResponseEntity.ok(businessRole.getAll());
	}
	
	@GetMapping(path = "showrole/{idRole}")
	public ResponseEntity<Map<String, Object>> getById(@PathVariable String idRole){
		
		return ResponseEntity.ok(businessRole.getById(idRole));
	}
	
	@DeleteMapping(path = "deleterole/{idRole}")
	public ResponseEntity<ResponseRoleDeleteById> deleteById(@PathVariable String idRole){
		ResponseRoleDeleteById response = businessRole.deleteById(idRole);
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping(path = "updaterole/{idRole}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseRoleUpdate> update(@PathVariable String idRole, @Valid @RequestBody RequestRoleUpdate resquest){
		ResponseRoleUpdate response = businessRole.update(idRole, resquest);
		
		return ResponseEntity.ok(response);
	}
}
