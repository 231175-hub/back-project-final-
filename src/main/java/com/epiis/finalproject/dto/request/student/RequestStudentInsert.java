package com.epiis.finalproject.dto.request.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestStudentInsert {
	@NotBlank(message = "El campo firstName es obligatorio")
	private String firstName;

	@NotBlank(message = "El campo surName es obligatorio")
	private String surName;

	@NotBlank(message = "El campo email es obligatorio")
	@Email(message = "Formato de correo electrónico inválido")
	private String email;

	@NotBlank(message = "El campo password es obligatorio")
	private String password;
	
	@NotBlank(message = "El campo code es obligatorio")
	private String code;
	
	private int totalCredits;

	@NotBlank(message = "El campo idSchool es obligatorio")
	private String idSchool;
}
