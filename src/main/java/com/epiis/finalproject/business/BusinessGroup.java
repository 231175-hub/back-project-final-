package com.epiis.finalproject.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.epiis.finalproject.dto.request.group.RequestGroupAssignment;
import com.epiis.finalproject.dto.request.group.RequestGroupInsert;
import com.epiis.finalproject.dto.request.group.RequestGroupUpdate;
import com.epiis.finalproject.dto.response.group.ProfessorGroupProjection;
import com.epiis.finalproject.dto.response.group.ResponseGroupDeleteById;
import com.epiis.finalproject.dto.response.group.ResponseGroupGetAll;
import com.epiis.finalproject.dto.response.group.ResponseGroupGetById;
import com.epiis.finalproject.dto.response.group.ResponseGroupInsert;
import com.epiis.finalproject.dto.response.group.ResponseGroupUpdate;
import com.epiis.finalproject.dto.response.group.ResponseProfessorGroups;
import com.epiis.finalproject.dto.response.groupstudent.ResponseGroupStudentInsert;
import com.epiis.finalproject.dto.response.student.ResponseUnassignedStudent;
import com.epiis.finalproject.entity.EntityAcademicPeriod;
import com.epiis.finalproject.entity.EntityCourse;
import com.epiis.finalproject.entity.EntityCourseEnrollment;
import com.epiis.finalproject.entity.EntityGroup;
import com.epiis.finalproject.entity.EntityGroupStudent;
import com.epiis.finalproject.entity.EntityProfessor;
import com.epiis.finalproject.entity.EntityUnits;
import com.epiis.finalproject.repository.RepositoryAcademicperiod;
import com.epiis.finalproject.repository.RepositoryCourse;
import com.epiis.finalproject.repository.RepositoryCourseEnrollment;
import com.epiis.finalproject.repository.RepositoryGroup;
import com.epiis.finalproject.repository.RepositoryGroupStudent;
import com.epiis.finalproject.repository.RepositoryUnits;
import com.epiis.finalproject.staticdata.EnumAcademicPeriod;

import jakarta.transaction.Transactional;

@Service
public class BusinessGroup {
	private final RepositoryGroup repositoryGroup;
	private final RepositoryGroupStudent repositoryGroupStudent;
    private final RepositoryCourseEnrollment repositoryCourseEnrollment;
    private final RepositoryAcademicperiod repositoryAcademicperiod;
    private final RepositoryCourse repositoryCourse;
    private final RepositoryUnits repositoryUnits;
	
	public BusinessGroup(RepositoryGroup repositoryGroup, RepositoryGroupStudent repositoryGroupStudent, RepositoryCourseEnrollment repositoryCourseEnrollment, RepositoryAcademicperiod repositoryAcademicperiod, RepositoryCourse repositoryCourse, RepositoryUnits repositoryUnits) {
		this.repositoryGroup = repositoryGroup;
		this.repositoryGroupStudent = repositoryGroupStudent;
		this.repositoryCourseEnrollment = repositoryCourseEnrollment;
		this.repositoryAcademicperiod = repositoryAcademicperiod;
		this.repositoryCourse = repositoryCourse;
		this.repositoryUnits = repositoryUnits;
	}
	
	public ResponseGroupInsert insert(RequestGroupInsert request) {
		ResponseGroupInsert response = new ResponseGroupInsert();
		
		EntityGroup entityGroup = new EntityGroup();
		EntityAcademicPeriod entityAcademicPeriod = new EntityAcademicPeriod();
		EntityProfessor entityProfessor = new EntityProfessor();
		EntityCourse entityCourse = new EntityCourse();
		
		entityAcademicPeriod.setIdPeriod(request.getIdPeriod());
		entityProfessor.setIdProfessor(request.getIdProfessor());
		entityCourse.setIdCourse(request.getIdCourse());
		
		entityGroup.setIdGroup(UUID.randomUUID().toString());
		entityGroup.setNameGroup(request.getNameGroup());
		entityGroup.setParentAcademicperiod(entityAcademicPeriod);
		entityGroup.setParentProfessor(entityProfessor);
		entityGroup.setParentCourse(entityCourse);
		entityGroup.setIdSheet(request.getIdSheet());
		entityGroup.setConceptualWeight(request.getConceptualWeight() == 0 ? 0.4 : request.getConceptualWeight());
		entityGroup.setPracticalWeight(request.getPracticalWeight() == 0 ? 0.4 : request.getPracticalWeight());
		entityGroup.setAttitudinalWeight(request.getAttitudinalWeight() == 0 ? 0.2 : request.getAttitudinalWeight());
		entityGroup.setCreatedAt(new java.sql.Date(new Date().getTime()));
		entityGroup.setUpdatedAt(entityGroup.getCreatedAt());
		
		repositoryGroup.save(entityGroup);
		
		createDefaultUnitsForGroup(entityGroup, entityGroup.getCreatedAt());
		
		response.success();
		response.getListMessage().add("Grupo Registrado Exitosamente");
		
		return response;
	}
	
	public Map<String, Object> getAll() {
		ResponseGroupGetAll response = new ResponseGroupGetAll();
		
		Map<String, Object> res = new HashMap<>();
		
		List<EntityGroup> entityGroup = repositoryGroup.findAll();
		
		response.success();
		response.getListMessage().add("Grupos Entregados Correctamente");
		
		res.put("message", response);
		res.put("data", entityGroup);
		
		return res;
	}
	
	public Map<String, Object> getById(String idGroup) {
		ResponseGroupGetById response = new ResponseGroupGetById();
		
		Map<String, Object> res = new HashMap<>();
		
		Optional<EntityGroup> entityGroup = repositoryGroup.findById(idGroup);
		
		response.success();
		response.getListMessage().add("Grupo Encontrado Correctamente");
		
		res.put("message", response);
		res.put("data", entityGroup);
		
		return res;
	}
	
	public ResponseGroupDeleteById deleteById(String idGroup) {
		ResponseGroupDeleteById response = new ResponseGroupDeleteById();
		
		repositoryGroup.deleteById(idGroup);
		
		response.success();
		response.getListMessage().add("Grupo Eliminado Exitosamente");
		
		return response;
	}
	
	public ResponseGroupUpdate update(String idGroup, RequestGroupUpdate request) {
		ResponseGroupUpdate response = new ResponseGroupUpdate();
		
		Optional<EntityGroup> optional = repositoryGroup.findById(idGroup);
		
		if (optional.isPresent()) {
			EntityGroup entityGroup = optional.get();
			
			EntityAcademicPeriod entityAcademicPeriod = new EntityAcademicPeriod();
			EntityProfessor entityProfessor = new EntityProfessor();
			EntityCourse entityCourse = new EntityCourse();
			
			entityAcademicPeriod.setIdPeriod(request.getIdPeriod());
			entityProfessor.setIdProfessor(request.getIdProfessor());
			entityCourse.setIdCourse(request.getIdCourse());
			
			entityGroup.setNameGroup(request.getNameGroup());
			entityGroup.setParentAcademicperiod(entityAcademicPeriod);
			entityGroup.setParentProfessor(entityProfessor);
			entityGroup.setParentCourse(entityCourse);
			entityGroup.setIdSheet(request.getIdSheet());
			entityGroup.setConceptualWeight(request.getConceptualWeight() == 0 ? 0.4 : request.getConceptualWeight());
			entityGroup.setPracticalWeight(request.getPracticalWeight() == 0 ? 0.4 : request.getPracticalWeight());
			entityGroup.setAttitudinalWeight(request.getAttitudinalWeight() == 0 ? 0.2 : request.getAttitudinalWeight());
			entityGroup.setUpdatedAt(new java.sql.Date(new Date().getTime()));
			
			repositoryGroup.save(entityGroup);
			
			response.success();
			response.getListMessage().add("Grupo Actualizado Correctamente");
			
			return response;
		}
		
		response.error();
		response.getListMessage().add("Error el Grupo no se Actualizo");
		
		return response;
	}
	
	@Transactional
	public ResponseGroupStudentInsert createAndAssignGroups(RequestGroupAssignment request) {
		ResponseGroupStudentInsert response = new ResponseGroupStudentInsert();
        
        Optional<EntityAcademicPeriod> optionalActivePeriod = repositoryAcademicperiod.findByStatus(EnumAcademicPeriod.ACTIVE.toString());
        if (optionalActivePeriod.isEmpty()) {
            response.error();
            response.getListMessage().add("Error: No hay un periodo académico activo actualmente en el sistema.");
            return response;
        }
        EntityAcademicPeriod activePeriod = optionalActivePeriod.get();

        List<EntityCourseEnrollment> unassignedStudents = repositoryCourseEnrollment.findUnassignedStudents(request.getIdCourse(), activePeriod.getIdPeriod());

        if (unassignedStudents.isEmpty()) {
            response.error();
            response.getListMessage().add("No hay estudiantes pendientes por asignar en este curso.");
            return response;
        }

        int totalStudents = unassignedStudents.size();
        int numberOfGroups = (int) Math.ceil((double) totalStudents / 30.0);

        Collections.shuffle(unassignedStudents);

        List<EntityGroup> newGroups = new ArrayList<>();
        Date currentDate = new Date();

        for (int i = 1; i <= numberOfGroups; i++) {
            EntityGroup group = new EntityGroup();
            group.setIdGroup(UUID.randomUUID().toString());

            char letraGrupo = (char) ('A' + i - 1);
            group.setNameGroup(String.valueOf(letraGrupo));

            group.setParentCourse(repositoryCourse.getReferenceById(request.getIdCourse()));
            group.setParentAcademicperiod(activePeriod);
            group.setConceptualWeight(0.4);
            group.setPracticalWeight(0.4);
            group.setAttitudinalWeight(0.2);
            group.setCreatedAt(currentDate);
            group.setUpdatedAt(currentDate);
            
            newGroups.add(group);
        }
        newGroups = repositoryGroup.saveAll(newGroups);

        List<EntityGroupStudent> assignmentsToSave = new ArrayList<>();
        int currentGroupIndex = 0;

        for (EntityCourseEnrollment enrollment : unassignedStudents) {
            EntityGroup currentGroup = newGroups.get(currentGroupIndex);

            EntityGroupStudent groupStudent = new EntityGroupStudent();
            groupStudent.setIdGroupStudent(UUID.randomUUID().toString());
            groupStudent.setParentGroup(currentGroup);
            groupStudent.setParentStudent(enrollment.getParentStudent());
            groupStudent.setCreatedAt(currentDate);
            groupStudent.setUpdatedAt(currentDate);
            
            assignmentsToSave.add(groupStudent);
            
            enrollment.setAssigned(true);

            currentGroupIndex = (currentGroupIndex + 1) % newGroups.size();
        }

        repositoryGroupStudent.saveAll(assignmentsToSave);
        repositoryCourseEnrollment.saveAll(unassignedStudents);

        response.success();
        response.getListMessage().add("Se crearon " + numberOfGroups + " grupos y se asignaron " + totalStudents + " estudiantes al azar.");
        return response;
    }
	
	public List<ResponseUnassignedStudent> getUnassignedStudents(String idCourse) {
        EntityAcademicPeriod activePeriod = repositoryAcademicperiod.findByStatus(EnumAcademicPeriod.ACTIVE.toString()).orElseThrow(() -> new RuntimeException("Error: No hay un periodo académico activo actualmente"));

        List<EntityCourseEnrollment> unassignedEnrollments = repositoryCourseEnrollment
                .findUnassignedStudents(idCourse, activePeriod.getIdPeriod());

        return unassignedEnrollments.stream().map(enrollment -> {
            String fullName = enrollment.getParentStudent().getParentUser().getFirstName() + " " + 
                              enrollment.getParentStudent().getParentUser().getSurName();
            
            return new ResponseUnassignedStudent(
                    enrollment.getParentStudent().getIdStudent(),
                    enrollment.getParentStudent().getCode(),
                    fullName
            );
        }).collect(Collectors.toList());
    }
	
	public List<EntityGroup> getGroupsByCourse(String idCourse) {
        return repositoryGroup.findAllGroupsByCourse(idCourse);
    }
	
	public ResponseProfessorGroups getGroupsByProfessor(String idProfessor) {
        ResponseProfessorGroups response = new ResponseProfessorGroups();

        try {
            List<ProfessorGroupProjection> groups = repositoryGroup.findGroupsByProfessor(idProfessor);
            
            response.setData(groups);
            response.success();
            response.getListMessage().add("Grupos obtenidos correctamente");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.error();
            response.getListMessage().add("Error al obtener los grupos: " + e.getMessage());
        }

        return response;
    }

    private void createDefaultUnitsForGroup(EntityGroup group, Date currentDate) {
        for (int i = 1; i <= 3; i++) {
            EntityUnits unit = new EntityUnits();
            unit.setIdUnits(UUID.randomUUID().toString());
            unit.setNumberUnit(i);
            unit.setNameUnit("Unidad " + i);
            unit.setParentGroup(group);
            unit.setCreatedAt(currentDate);
            unit.setUpdatedAt(currentDate);
            repositoryUnits.save(unit);
        }
    }

	@org.springframework.transaction.annotation.Transactional
	public ResponseGroupUpdate updateWeights(String idGroup, double conceptual, double practical, double attitudinal) {
		ResponseGroupUpdate response = new ResponseGroupUpdate();
		Optional<EntityGroup> optional = repositoryGroup.findById(idGroup);
		if (optional.isPresent()) {
			EntityGroup group = optional.get();
			group.setConceptualWeight(conceptual);
			group.setPracticalWeight(practical);
			group.setAttitudinalWeight(attitudinal);
			group.setUpdatedAt(new Date());
			repositoryGroup.save(group);
			response.success();
			response.getListMessage().add("Pesos actualizados correctamente");
			return response;
		}
		response.error();
		response.getListMessage().add("Grupo no encontrado");
		return response;
	}
}
