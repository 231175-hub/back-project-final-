package com.epiis.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epiis.finalproject.entity.EntityAcademicPeriod;
import java.util.Optional;


public interface RepositoryAcademicperiod extends JpaRepository<EntityAcademicPeriod, String>{
	Optional<EntityAcademicPeriod> findByStatus(String status);
}
