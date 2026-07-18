package com.epiis.finalproject.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tschool")
@Setter
@Getter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EntitySchool {
	@Id
	@Column(name = "id_school")
	private String idSchool;
	
	@Column(name = "name_school")
	private String nameSchool;
	
	@Column(name = "urlImageSchool")
	private String urlImageSchool;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
	
	@OneToMany(mappedBy = "parentSchool", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<EntityCourse> childCourse;
	
	@OneToMany(mappedBy = "parentSchool", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<EntityStudent> childStudent;
	
	@OneToMany(mappedBy = "parentSchool", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<EntitySchoolfile> childSchoolfile;
}
