package com.epiis.finalproject.dto.request.role;
import jakarta.validation.constraints.NotBlank;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestRoleUpdate {
	@NotBlank(message = "El campo nameRole es obligatorio")
	private String nameRole;
}
