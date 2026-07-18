package com.epiis.finalproject.dto.request.course;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestCourseInsert {
	@NotBlank(message = "El campo code es obligatorio")
	private String code;
	private int credits;
	@NotBlank(message = "El campo nameCourse es obligatorio")
	private String nameCourse;
	@NotBlank(message = "El campo category es obligatorio")
	private String category;
	@NotBlank(message = "El campo idSchool es obligatorio")
	private String idSchool;
	@NotNull(message = "El campo units es obligatorio")
	private List<Integer> units;
}
