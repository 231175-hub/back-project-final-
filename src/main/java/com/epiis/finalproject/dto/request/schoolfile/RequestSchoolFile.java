package com.epiis.finalproject.dto.request.schoolfile;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestSchoolFile {
	@NotBlank(message = "El campo idSchoolfile es obligatorio")
	private String idSchoolfile;
	@NotBlank(message = "El campo idSchool es obligatorio")
	private String idSchool;
	@NotBlank(message = "El campo name es obligatorio")
	private String name;
	@NotBlank(message = "El campo extension es obligatorio")
	private String extension;
}
