package com.epiis.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.epiis.finalproject.entity.EntityGradeLog;

public interface RepositoryGradeLog extends JpaRepository<EntityGradeLog, String> {
}
