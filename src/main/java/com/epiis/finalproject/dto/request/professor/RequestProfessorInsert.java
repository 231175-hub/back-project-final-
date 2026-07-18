package com.epiis.finalproject.dto.request.professor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestProfessorInsert {
	@NotBlank(message = "El campo firstName es obligatorio")
	private String firstName;

	@NotBlank(message = "El campo surName es obligatorio")
	private String surName;

	@NotBlank(message = "El campo email es obligatorio")
	@Email(message = "Formato de correo electrónico inválido")
	private String email;

	@NotBlank(message = "El campo password es obligatorio")
	private String password;
}
