package com.epiis.finalproject.dto.request.professor;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestProfessorUpdate extends RequestProfessorInsert {
	@NotBlank(message = "El campo idRole es obligatorio")
	private String idRole;

	@NotBlank(message = "El campo idUser es obligatorio")
	private String idUser;
}
