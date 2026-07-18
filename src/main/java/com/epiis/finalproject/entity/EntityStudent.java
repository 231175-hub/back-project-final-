package com.epiis.finalproject.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tstudent")
@SQLDelete(sql = "UPDATE tstudent SET deleted = true WHERE id_student = ?")
@Where(clause = "deleted = false")
@Setter
@Getter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EntityStudent {
	@Column(name = "deleted")
	private boolean deleted = false;

	@Id
	@Column(name = "id_student")
	private String idStudent;
	
	@Column(name = "code")
	private String code;
	
	@Column(name = "number_courses")
	private int numberCourses;
	
	@Column(name = "average")
	private double average;
	
	@Column(name = "ranking")
	private int ranking;
	
	
	@Column(name = "total_credits")
	private int totalCredits;
	
	@Column(name = "status")
	private String status;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_school")
	@JsonIgnoreProperties("childStudent")
	private EntitySchool parentSchool;
	
	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "id_student")
	@JsonIgnoreProperties("childStudent")
	private EntityUser parentUser;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
	
	@OneToMany(mappedBy = "parentStudent", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<EntityGroupStudent> childGroupStudent;
	
	@OneToMany(mappedBy = "parentStudent", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<EntityCourseEnrollment> childCourseEnrollment;
}
