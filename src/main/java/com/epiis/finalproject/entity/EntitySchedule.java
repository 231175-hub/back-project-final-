package com.epiis.finalproject.entity;

import java.time.LocalTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tschedule")
@Setter
@Getter
public class EntitySchedule {
	@Id
	@Column(name = "id_schedule")
	private String idSchedule;
	
	@Column(name = "day_week")
	private String dayWeek;
	
	@Column(name = "start_time")
	private LocalTime startTime;
	
	@Column(name = "end_time")
	private LocalTime endTime;
	
	@Column(name = "classroom")
	private String classroom;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_group")
	@JsonBackReference
	private EntityGroup parentGroup;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
}
