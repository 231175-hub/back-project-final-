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
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tgroup_student")
@Setter
@Getter
public class EntityGroupStudent {
	@Version
	@Column(name = "version")
	private Integer version;

	@Id
	@Column(name = "id_group_student")
	private String idGroupStudent;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_group")
	@JsonIgnore
	private EntityGroup parentGroup;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_student")
	@JsonIgnore
	private EntityStudent parentStudent;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
	
	@OneToMany(mappedBy = "parentGroupStudent", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<EntityUnitscore> childUnitscore;

	@OneToMany(mappedBy = "parentGroupStudent", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<EntityAttendance> childAttendance;
}
