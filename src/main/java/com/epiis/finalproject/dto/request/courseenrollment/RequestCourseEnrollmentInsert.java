package com.epiis.finalproject.dto.request.courseenrollment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestCourseEnrollmentInsert {
	@NotBlank(message = "El campo idStudent es obligatorio")
	private String idStudent;
	@NotNull(message = "El campo courses es obligatorio")
	private List<String> courses;
}
