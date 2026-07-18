package com.epiis.finalproject.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tunitscore")
@Setter
@Getter
public class EntityUnitscore {
	@Version
	@Column(name = "version")
	private Integer version;

	@Id
	@Column(name = "id_unitscore")
	private String idUnitScore;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_group_student")
	@JsonIgnore
	private EntityGroupStudent parentGroupStudent;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_units")
	@JsonIgnore
	private EntityUnits parentUnits;
	
	@Column(name = "score")
	private double score;
	
	@Column(name = "conceptual_score")
	private Double conceptualScore;
	
	@Column(name = "practical_score")
	private Double practicalScore;
	
	@Column(name = "test1_score")
	private Double test1Score;
	
	@Column(name = "test2_score")
	private Double test2Score;
	
	@Column(name = "attitudinal_score")
	private Double attitudinalScore;
	
	@Column(name = "conceptual_grades")
	private String conceptualGrades;
	
	@Column(name = "practical_grades")
	private String practicalGrades;
	
	@Column(name = "test_grades")
	private String testGrades;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
}
