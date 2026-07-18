package com.epiis.finalproject.dto.request.unitscore;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestUnitScoreInsert {
	@NotBlank(message = "El campo idGroupStudent es obligatorio")
	private String idGroupStudent;
	@NotBlank(message = "El campo idUnits es obligatorio")
	private String idUnits;
	private double score;
}
