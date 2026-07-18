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
@Table(name = "tcourse")
@Setter
@Getter
public class EntityCourse {
	@Id
	@Column(name = "id_course")
	private String idCourse;
	
	@Column(name = "code")
	private String code;
	
	@Column(name = "credits")
	private int credits;
	
	@Column(name = "name_course")
	private String nameCourse;
	@Column(name = "category")
	private String category;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_school")
	@JsonIgnore
	private EntitySchool parentSchool;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
	
	@OneToMany(mappedBy = "parentCourse", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<EntityGroup> childGroup;
	
	
	@OneToMany(mappedBy = "parentCourse", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<EntityCourseEnrollment> childCourseEnrollment;
}
