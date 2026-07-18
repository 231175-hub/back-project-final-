package com.epiis.finalproject.dto.request.schoolfile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestSchoolFileInsert {
	@NotBlank(message = "El campo idSchool es obligatorio")
	private String idSchool;
	@NotNull(message = "El campo files es obligatorio")
	private MultipartFile[] files;
}
