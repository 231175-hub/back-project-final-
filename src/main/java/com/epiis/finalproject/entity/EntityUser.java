package com.epiis.finalproject.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tuser")
@Setter
@Getter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EntityUser {
	@Id
	@Column(name = "id_user")
	private String idUser;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "sur_name")
	private String surName;

	@Column(name = "email")
	private String email;

	@Column(name = "urlImageProfile")
	private String urlImageProfile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_role")
	@JsonBackReference
	private EntityRole parentRole;

	@Column(name = "created_at")
	private Date createdAt;

	@Column(name = "updated_at")
	private Date updatedAt;

	@OneToOne(mappedBy = "parentUser", cascade = CascadeType.ALL)
	@JsonIgnoreProperties("parentUser")
	private EntityProfessor childProfessor;

	@OneToOne(mappedBy = "parentUser", cascade = CascadeType.ALL)
	@JsonIgnoreProperties("parentUser")
	private EntityStudent childStudent;
}
