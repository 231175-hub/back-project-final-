package com.epiis.finalproject.entity;

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
@Table(name = "tcourse_enrollment")
@Setter
@Getter
public class EntityCourseEnrollment {
	@Id
	@Column(name = "id_enrollment")
	private String idEnrollment;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_student")
	@JsonBackReference
	private EntityStudent parentStudent;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_course")
	@JsonBackReference
	private EntityCourse parentCourse;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_period")
	@JsonBackReference
	private EntityAcademicPeriod parentPeriod;
	
	@Column(name = "is_assigned")
	private boolean isAssigned;
	
	@Column(name = "final_score")
	private Integer finalScore;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
}
