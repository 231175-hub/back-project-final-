package com.epiis.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epiis.finalproject.entity.EntityProfessor;

public interface RepositoryProfessor extends JpaRepository<EntityProfessor, String>{}
