package com.epiis.finalproject.controller;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epiis.finalproject.business.BusinessCourseEnrollment;
import com.epiis.finalproject.dto.request.courseenrollment.RequestCourseEnrollmentInsert;
import com.epiis.finalproject.dto.response.courseenrollment.ResponseCourseEnrollmentInsert;

import org.springframework.web.bind.annotation.RequestBody;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping(path = "intranet")
public class CourseEnrollmentController {
	
	private final BusinessCourseEnrollment businessCourseEnrollment;
	
	public CourseEnrollmentController(BusinessCourseEnrollment businessCourseEnrollment) {
		this.businessCourseEnrollment = businessCourseEnrollment;
	}
	
	@PostMapping(path = "registercourseenrollment", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseCourseEnrollmentInsert> insert(@Valid @RequestBody RequestCourseEnrollmentInsert request){
		ResponseCourseEnrollmentInsert response = businessCourseEnrollment.insert(request);
		
		return ResponseEntity.ok(response);
	}
}
