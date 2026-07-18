package com.epiis.finalproject.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.epiis.finalproject.entity.EntityAttendance;
import com.epiis.finalproject.entity.EntityGroupStudent;

public interface RepositoryAttendance extends JpaRepository<EntityAttendance, String> {
    List<EntityAttendance> findByParentGroupStudentIn(List<EntityGroupStudent> groupStudents);
}
