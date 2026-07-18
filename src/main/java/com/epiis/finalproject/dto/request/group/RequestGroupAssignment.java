package com.epiis.finalproject.dto.request.group;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestGroupAssignment {
	@NotBlank(message = "El campo idCourse es obligatorio")
	private String idCourse;
}
