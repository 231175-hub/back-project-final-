package com.epiis.finalproject.dto.request.schedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import com.epiis.finalproject.entity.EntitySchedule;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestScheduleInsert {
	@NotBlank(message = "El campo idGroup es obligatorio")
	private String idGroup;
	@NotBlank(message = "El campo idProfessor es obligatorio")
	private String idProfessor;
	@NotNull(message = "El campo schedules es obligatorio")
	private List<EntitySchedule> schedules;
}
