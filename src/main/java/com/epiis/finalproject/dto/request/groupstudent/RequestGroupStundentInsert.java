package com.epiis.finalproject.dto.request.groupstudent;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestGroupStundentInsert {
	@NotBlank(message = "El campo idGroup es obligatorio")
	private String idGroup;
	@NotBlank(message = "El campo idStudent es obligatorio")
	private String idStudent;
	
	@NotBlank(message = "El campo idCourse es obligatorio")
	private String idCourse;
}
