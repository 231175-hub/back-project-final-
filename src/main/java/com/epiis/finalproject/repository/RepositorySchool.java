package com.epiis.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epiis.finalproject.entity.EntitySchool;

public interface RepositorySchool extends JpaRepository<EntitySchool, String> {
}
