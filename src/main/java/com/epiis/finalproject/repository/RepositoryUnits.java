package com.epiis.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epiis.finalproject.entity.EntityUnits;

public interface RepositoryUnits extends JpaRepository<EntityUnits, String>{
	@Query("SELECT COUNT(u) FROM EntityUnits u WHERE u.parentGroup.idGroup = :idGroup")
	int countByGroup(@Param("idGroup") String idGroup);

	java.util.List<EntityUnits> findByParentGroupOrderByNumberUnitAsc(com.epiis.finalproject.entity.EntityGroup parentGroup);

	java.util.List<EntityUnits> findByParentGroupInOrderByNumberUnitAsc(java.util.Collection<com.epiis.finalproject.entity.EntityGroup> parentGroups);
}
