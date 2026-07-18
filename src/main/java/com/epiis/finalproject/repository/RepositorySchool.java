package com.epiis.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epiis.finalproject.entity.EntitySchool;

@Repository
public interface RepositorySchool extends JpaRepository<EntitySchool, String>{}
