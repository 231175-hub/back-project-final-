package com.epiis.finalproject.dto.request.academicperiod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestAcademicPeriodUpdate {
	private int yearPeriod;
	private int numberPeriod;
	@NotNull(message = "El campo startDate es obligatorio")
	private Date startDate;
	@NotNull(message = "El campo endDate es obligatorio")
	private Date endDate;
	@NotBlank(message = "El campo status es obligatorio")
	private String status;
}
