package com.epiis.finalproject.dto.request.course;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestCourseUpdate {
	@NotBlank(message = "El campo code es obligatorio")
	private String code;
	private int credits;
	@NotBlank(message = "El campo nameCourse es obligatorio")
	private String nameCourse;
	@NotBlank(message = "El campo idSchool es obligatorio")
	private String idSchool;
	@NotBlank(message = "El campo category es obligatorio")
	private String category;
}
