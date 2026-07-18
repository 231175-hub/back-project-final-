package com.epiis.finalproject.dto.request.school;
import jakarta.validation.constraints.NotBlank;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestSchoolInsert {
	@NotBlank(message = "El campo nameSchool es obligatorio")
	private String nameSchool;
	private MultipartFile file;
}
