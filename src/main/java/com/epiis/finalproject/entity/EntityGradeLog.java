package com.epiis.finalproject.entity;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tgrade_log")
@Setter
@Getter
public class EntityGradeLog {
	@Id
	@Column(name = "id_grade_log")
	private String idGradeLog;

	@Column(name = "id_unitscore")
	private String idUnitScore;

	@Column(name = "field_name")
	private String fieldName;

	@Column(name = "previous_score")
	private Double previousScore;

	@Column(name = "new_score")
	private Double newScore;

	@Column(name = "modified_by")
	private String modifiedBy;

	@Column(name = "created_at")
	private Date createdAt;
}
