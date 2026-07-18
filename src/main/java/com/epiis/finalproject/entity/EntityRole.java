package com.epiis.finalproject.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "trole")
@Setter
@Getter
public class EntityRole {
	@Id
	@Column(name = "id_role")
	private String idRole;
	
	@Column(name = "name_role")
	private String nameRole;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
	
	@OneToMany(mappedBy = "parentRole", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<EntityUser> childUser;
}
