package com.epiis.finalproject.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
@Table(name = "tunits")
@Setter
@Getter
public class EntityUnits {
	@Id
	@Column(name = "id_units")
	private String idUnits;
	
	@Column(name = "number_unit")
	private int numberUnit;
	
	@Column(name = "name_unit")
	private String nameUnit;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_group")
	@JsonBackReference
	private EntityGroup parentGroup;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
	
	@OneToMany(mappedBy = "parentUnits", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<EntityUnitscore> childUnitscore; 
}