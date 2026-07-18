package com.epiis.finalproject.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tgroup")
@Setter
@Getter
public class EntityGroup {
	@Id
	@Column(name = "id_group")
	private String idGroup;
	
	@Column(name = "name_group")
	private String nameGroup;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_period")
	@JsonIgnore
	private EntityAcademicPeriod parentAcademicperiod;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_professor")
	@JsonIgnore
	private EntityProfessor parentProfessor;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_course")
	@JsonIgnore
	private EntityCourse parentCourse;
	
	@Column(name = "id_sheet")
	private String idSheet;

	@Column(name = "conceptual_weight")
	private double conceptualWeight;

	@Column(name = "practical_weight")
	private double practicalWeight;

	@Column(name = "attitudinal_weight")
	private double attitudinalWeight;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
	
	@OneToMany(mappedBy = "parentGroup", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<EntitySchedule> childSchedule;
	
	@OneToMany(mappedBy = "parentGroup", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<EntityGroupStudent> childGroupStudent;
	
	@OneToMany(mappedBy = "parentGroup", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<EntityUnits> childUnits;
}
