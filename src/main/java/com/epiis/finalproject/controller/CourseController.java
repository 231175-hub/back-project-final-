package com.epiis.finalproject.controller;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

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

import com.epiis.finalproject.business.BusinessCourse;
import com.epiis.finalproject.dto.request.course.RequestCourseInsert;
import com.epiis.finalproject.dto.request.course.RequestCourseUpdate;
import com.epiis.finalproject.dto.response.course.ResponseCourseDeleteById;
import com.epiis.finalproject.dto.response.course.ResponseCourseInsert;
import com.epiis.finalproject.dto.response.course.ResponseCourseSearch;
import com.epiis.finalproject.dto.response.course.ResponseCourseUpdate;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping(path = "intranet")
public class CourseController {
	private final BusinessCourse businessCourse;
	
	public CourseController(BusinessCourse businessCourse) {
		this.businessCourse = businessCourse;
	}
	
	@PostMapping(path = "registercourse", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseCourseInsert> insert(@Valid @RequestBody RequestCourseInsert request){
		ResponseCourseInsert response = businessCourse.insert(request);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(path = "indexcourse")
	public ResponseEntity<Map<String, Object>> getAll(){
		
		return ResponseEntity.ok(businessCourse.getAll());
	}
	
	@GetMapping(path = "showcourse/{idCourse}")
	public ResponseEntity<Map<String, Object>> getById(@PathVariable String idCourse){
		
		return ResponseEntity.ok(businessCourse.getById(idCourse));
	}
	
	@DeleteMapping(path = "deletecourse/{idCourse}")
	public ResponseEntity<ResponseCourseDeleteById> deleteById(@PathVariable String idCourse){
		ResponseCourseDeleteById response = businessCourse.deleteById(idCourse);
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping(path = "updatecourse/{idCourse}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseCourseUpdate> update(@PathVariable String idCourse, @Valid @RequestBody RequestCourseUpdate resquest){
		ResponseCourseUpdate response = businessCourse.update(idCourse, resquest);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/searchCourse")
    public ResponseEntity<List<ResponseCourseSearch>> searchCourse(
            @RequestParam(value = "query", required = false, defaultValue = "") String query,
            @RequestParam("idSchool") String idSchool) {
        List<ResponseCourseSearch> result = businessCourse.searchCoursesForAutocomplete(query, idSchool);
        return ResponseEntity.ok(result);
    }
}
