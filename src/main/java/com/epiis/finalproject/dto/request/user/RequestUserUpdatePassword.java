package com.epiis.finalproject.dto.request.user;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestUserUpdatePassword {
	@NotBlank(message = "El campo password es obligatorio")
	private String password;
}
