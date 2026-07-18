package com.epiis.finalproject.dto.request.units;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestUnitsUpdate {
	private int numberUnit;
	@NotBlank(message = "El campo nameUnit es obligatorio")
	private String nameUnit;
	private double conceptualWeight;
	private double practicalWeight;
	private double attitudinalWeight;
	@NotBlank(message = "El campo idGroup es obligatorio")
	private String idGroup;
}
