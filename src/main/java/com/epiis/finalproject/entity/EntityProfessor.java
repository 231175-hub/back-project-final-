package com.epiis.finalproject.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tprofessor")
@SQLDelete(sql = "UPDATE tprofessor SET deleted = true WHERE id_professor = ?")
@Where(clause = "deleted = false")
@Setter
@Getter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EntityProfessor {
	@Column(name = "deleted")
	private boolean deleted = false;

	@Id
	@Column(name = "id_professor")
	private String idProfessor;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "id_professor")
	@JsonIgnoreProperties({"childProfessor", "hibernateLazyInitializer", "handler"})
	private EntityUser parentUser;

	@Column(name = "created_at")
	private Date createdAt;

	@Column(name = "updated_at")
	private Date updatedAt;

	@OneToMany(mappedBy = "parentProfessor", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<EntityGroup> childGroup;
}
