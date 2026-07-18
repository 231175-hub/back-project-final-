package com.epiis.finalproject.dto.request.schedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestScheduleUpdate {
	@NotBlank(message = "El campo dayWeek es obligatorio")
	private String dayWeek;
	@NotNull(message = "El campo startTime es obligatorio")
	private LocalTime startTime;
	@NotNull(message = "El campo endTime es obligatorio")
	private LocalTime endTime;
	@NotBlank(message = "El campo classroom es obligatorio")
	private String classroom;
	@NotBlank(message = "El campo idGroup es obligatorio")
	private String idGroup;
}
