package com.epiis.finalproject.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tacademic_period")
@Setter
@Getter
public class EntityAcademicPeriod {
	@Id
	@Column(name = "id_period")
	private String idPeriod;
	
	@Column(name = "year_period")
	private int yearPeriod;
	
	@Column(name = "number_period")
	private int numberPeriod;
	
	@Column(name = "start_date")
	private Date startDate;
	
	@Column(name = "end_date")
	private Date endDate;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
	
	@OneToMany(mappedBy = "parentAcademicperiod", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<EntityGroup> chilEntityGroup;
	
	@OneToMany(mappedBy = "parentPeriod", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<EntityCourseEnrollment> childCourseEnrollment;
}
