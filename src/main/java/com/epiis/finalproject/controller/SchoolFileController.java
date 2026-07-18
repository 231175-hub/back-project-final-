package com.epiis.finalproject.controller;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epiis.finalproject.business.BusinessSchoolFile;
import com.epiis.finalproject.dto.request.schoolfile.RequestSchoolFileInsert;
import com.epiis.finalproject.dto.response.school.ResponseSchoolFileInsert;
import com.epiis.finalproject.entity.EntitySchoolfile;

@PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
@RestController
@RequestMapping(path = "intranet")
public class SchoolFileController {
	private final BusinessSchoolFile businessSchoolFile;
	
	public SchoolFileController(BusinessSchoolFile businessSchoolFile) {
		this.businessSchoolFile = businessSchoolFile;
	}
	
	@PostMapping(path = "registerschoolfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ResponseSchoolFileInsert> insert(@ModelAttribute RequestSchoolFileInsert request) throws Exception {
		ResponseSchoolFileInsert response = businessSchoolFile.insert(request);
		
		return ResponseEntity.ok(response);
	}

	@GetMapping(path = "indexschoolfile/{idSchool}")
	public ResponseEntity<List<EntitySchoolfile>> getBySchool(@PathVariable String idSchool) {
		List<EntitySchoolfile> files = businessSchoolFile.getBySchool(idSchool);
		return ResponseEntity.ok(files);
	}

	@DeleteMapping(path = "deleteschoolfile/{idSchoolFile}")
	public ResponseEntity<Void> deleteById(@PathVariable String idSchoolFile) {
		businessSchoolFile.deleteById(idSchoolFile);
		return ResponseEntity.ok().build();
	}
}
