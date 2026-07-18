package com.epiis.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epiis.finalproject.entity.EntitySchoolfile;

public interface RepositorySchoolFile extends JpaRepository<EntitySchoolfile, String>{}