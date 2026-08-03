package com.epiis.finalproject.dto.request.student;

import com.epiis.finalproject.dto.request.BasePersonRequest;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestStudentInsert extends BasePersonRequest {
	@NotBlank(message = "El campo code es obligatorio")
	private String code;

	private int totalCredits;

	@NotBlank(message = "El campo idSchool es obligatorio")
	private String idSchool;
}
