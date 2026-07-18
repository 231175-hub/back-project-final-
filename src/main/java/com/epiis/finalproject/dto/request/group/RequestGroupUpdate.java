package com.epiis.finalproject.dto.request.group;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestGroupUpdate {
	@NotBlank(message = "El campo nameGroup es obligatorio")
	private String nameGroup;
	@NotBlank(message = "El campo idPeriod es obligatorio")
	private String idPeriod;
	@NotBlank(message = "El campo idProfessor es obligatorio")
	private String idProfessor;
	@NotBlank(message = "El campo idCourse es obligatorio")
	private String idCourse;
	@NotBlank(message = "El campo idSheet es obligatorio")
	private String idSheet;
	private double conceptualWeight;
	private double practicalWeight;
	private double attitudinalWeight;
}
