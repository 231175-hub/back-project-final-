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
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tattendance")
@Setter
@Getter
public class EntityAttendance {
	@Version
	@Column(name = "version")
	private Integer version;

	@Id
	@Column(name = "id_attendance")
	private String idAttendance;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_group_student")
	@JsonBackReference
	private EntityGroupStudent parentGroupStudent;
	
	@Column(name = "attendance_date")
	private Date attendanceDate;
	
	@Column(name = "status")
	private String status; // 'A', 'F', 'T', 'J'
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
}
