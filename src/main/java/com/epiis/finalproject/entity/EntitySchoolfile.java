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
@Table(name = "tschoolfile")
@Setter
@Getter
public class EntitySchoolfile {
	@Id
	@Column(name = "id_schoolfile")
	private String idSchoolFile;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_school")
	@JsonBackReference
	private EntitySchool parentSchool;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "extension")
	private String extension;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
}
