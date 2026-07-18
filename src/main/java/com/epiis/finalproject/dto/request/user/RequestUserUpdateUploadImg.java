package com.epiis.finalproject.dto.request.user;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestUserUpdateUploadImg {
	@NotNull(message = "El campo file es obligatorio")
	private MultipartFile file;
}
