package com.epiis.finalproject.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.epiis.finalproject.helper.GradeCalculationHelper;
import com.epiis.finalproject.helper.PasswordUpdateHelper;
import com.epiis.finalproject.helper.DateUtil;
import com.epiis.finalproject.helper.ResponseMapBuilder;
import com.epiis.finalproject.helper.UserMutationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.epiis.finalproject.dto.request.student.RequestStudentInsert;
import com.epiis.finalproject.dto.request.student.RequestStudentUpdate;
import com.epiis.finalproject.dto.request.user.RequestUserUpdatePassword;
import com.epiis.finalproject.dto.response.student.ResponseStudentDeleteById;
import com.epiis.finalproject.dto.response.student.ResponseStudentGetAll;
import com.epiis.finalproject.dto.response.student.ResponseStudentGetById;
import com.epiis.finalproject.dto.response.student.ResponseStudentInsert;
import com.epiis.finalproject.dto.response.student.ResponseStudentSearch;
import com.epiis.finalproject.dto.response.student.ResponseStudentUpdate;
import com.epiis.finalproject.dto.response.user.ResponseUserUpdatePassword;
import com.epiis.finalproject.entity.EntityAttendance;
import com.epiis.finalproject.entity.EntityCourse;
import com.epiis.finalproject.entity.EntityGroup;
import com.epiis.finalproject.entity.EntityGroupStudent;
import com.epiis.finalproject.entity.EntityRole;
import com.epiis.finalproject.entity.EntitySchool;
import com.epiis.finalproject.entity.EntityStudent;
import com.epiis.finalproject.entity.EntityUnits;
import com.epiis.finalproject.entity.EntityUnitscore;
import com.epiis.finalproject.entity.EntityUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.epiis.finalproject.repository.RepositoryAttendance;
import com.epiis.finalproject.repository.RepositoryGroup;
import com.epiis.finalproject.repository.RepositoryGroupStudent;
import com.epiis.finalproject.repository.RepositoryRole;
import com.epiis.finalproject.repository.RepositorySchool;
import com.epiis.finalproject.repository.RepositoryStudent;
import com.epiis.finalproject.repository.RepositoryUnits;
import com.epiis.finalproject.repository.RepositoryUser;
import com.epiis.finalproject.staticdata.EnumRoles;
import com.epiis.finalproject.staticdata.EnumStudent;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class BusinessStudent {
	private static final Logger log = LoggerFactory.getLogger(BusinessStudent.class);
	private static final String KEY_MESSAGE = "message";
	private static final String KEY_SUCCESS = "success";
	private static final String KEY_ERROR = "error";

	private final RepositoryStudent repositoryStudent;
	private final RepositoryRole repositoryRole;
	private final RepositorySchool repositorySchool;
	private final RepositoryUser repositoryUser;
	private final PasswordEncoder passwordEncoder;
	private final RepositoryGroup repositoryGroup;
	private final RepositoryGroupStudent repositoryGroupStudent;
	private final RepositoryUnits repositoryUnits;
	private final RepositoryAttendance repositoryAttendance;
	private final EntityManager entityManager;
	private final PasswordUpdateHelper passwordUpdateHelper;
	
	public BusinessStudent(
			RepositoryStudent repositoryStudent, 
			RepositoryUser repositoryUser, 
			RepositoryRole repositoryRole, 
			RepositorySchool repositorySchool, 
			PasswordEncoder passwordEncoder, 
			RepositoryGroup repositoryGroup, 
			RepositoryGroupStudent repositoryGroupStudent,
			RepositoryUnits repositoryUnits,
			RepositoryAttendance repositoryAttendance,
			EntityManager entityManager,
			PasswordUpdateHelper passwordUpdateHelper) {
		this.repositoryStudent = repositoryStudent;
		this.repositoryUser = repositoryUser;
		this.repositoryRole = repositoryRole;
		this.repositorySchool = repositorySchool;
		this.passwordEncoder = passwordEncoder;
		this.repositoryGroup = repositoryGroup;
		this.repositoryGroupStudent = repositoryGroupStudent;
		this.repositoryUnits = repositoryUnits;
		this.repositoryAttendance = repositoryAttendance;
		this.entityManager = entityManager;
		this.passwordUpdateHelper = passwordUpdateHelper;
	}
	
	@Transactional
	public ResponseStudentInsert insert(RequestStudentInsert request) {
		ResponseStudentInsert response = new ResponseStudentInsert();
	    try {
	        EntityRole roleStudent = repositoryRole.findByNameRole(EnumRoles.STUDENT.toString()).orElse(null);
	        if (roleStudent == null) {
	        	response.error();
	        	response.getListMessage().add("Error interno: El rol STUDENT no está configurado en la BD.");
	        	return response;
	        }
	        
	        EntitySchool entitySchool = repositorySchool.findById(request.getIdSchool()).orElse(null);
	        if (entitySchool == null) {
	        	response.error();
	        	response.getListMessage().add("Error: La escuela especificada no existe en el sistema.");
	        	return response;
	        }

	        if (repositoryUser.findByEmail(request.getEmail()).isPresent()) {
	        	response.error();
	        	response.getListMessage().add("El correo electrónico ya está registrado.");
	        	return response;
	        }

	        if (repositoryStudent.findByCode(request.getCode()).isPresent()) {
	        	response.error();
	        	response.getListMessage().add("El código del estudiante ya está registrado.");
	        	return response;
	        }

	        EntityUser entityUser = UserMutationHelper.buildEntityUser(
	                request.getFirstName(), request.getSurName(), request.getEmail(), request.getPassword(), passwordEncoder);

	        entityUser.setParentRole(roleStudent);
	        
	        EntityStudent entityStudent = new EntityStudent();
	        entityStudent.setIdStudent(entityUser.getIdUser());
	        entityStudent.setCode(request.getCode());
	        entityStudent.setTotalCredits(request.getTotalCredits());
	        entityStudent.setStatus(EnumStudent.ACTIVE.toString());
	        
	        entityStudent.setParentUser(entityUser);
	        entityStudent.setParentSchool(entitySchool); 
	        
	        entityStudent.setCreatedAt(entityUser.getCreatedAt());
	        entityStudent.setUpdatedAt(entityUser.getCreatedAt());

	        entityUser.setChildStudent(entityStudent); // Bidirectional association

	        entityManager.persist(entityUser); // Persists both User and Student due to cascade
	       
	        response.success();
	        response.getListMessage().add("Estudiante registrado correctamente.");
	        
	    } catch (Exception e) {
	        log.error("Error al registrar el estudiante", e);
	        response.error();
	        response.getListMessage().add("Error al registrar el estudiante: " + e.getMessage());
	    }
	    
	    return response;
	}
	
	public Map<String, Object> getAll(){
		return ResponseMapBuilder.buildDataMap(
				new ResponseStudentGetAll(),
				"Estudiantes Entregados exitosamente",
				repositoryStudent.studentsWithSchoolAndUser());
	}
	
	public Map<String, Object> getById(String idStudent){
		ResponseStudentGetById response = new ResponseStudentGetById();
		Map<String, Object> res = new HashMap<>();
		Optional<EntityStudent> entityStudent = repositoryStudent.findByIdWithSchoolAndUser(idStudent);
		
		response.success();
		response.getListMessage().add("Estudiante Encontrado exitosamente");
		res.put(KEY_MESSAGE, response);
		
		if (entityStudent.isPresent()) {
			EntityStudent student = entityStudent.get();
			Map<String, Object> studentMap = new HashMap<>();
			studentMap.put("idStudent", student.getIdStudent());
			studentMap.put("code", student.getCode());
			
			if (student.getParentSchool() != null) {
				Map<String, Object> schoolMap = new HashMap<>();
				schoolMap.put("idSchool", student.getParentSchool().getIdSchool());
				schoolMap.put("nameSchool", student.getParentSchool().getNameSchool());
				schoolMap.put("urlImageSchool", student.getParentSchool().getUrlImageSchool());
				studentMap.put("parentSchool", schoolMap);
			}
			
			if (student.getParentUser() != null) {
				Map<String, Object> userMap = new HashMap<>();
				userMap.put("idUser", student.getParentUser().getIdUser());
				userMap.put("firstName", student.getParentUser().getFirstName());
				userMap.put("surName", student.getParentUser().getSurName());
				userMap.put("email", student.getParentUser().getEmail());
				studentMap.put("parentUser", userMap);
			}
			
			res.put("data", studentMap);
		} else {
			res.put("data", null);
		}
		
		return res;
	}
	
	public ResponseStudentDeleteById deleteById(String idStudent) {
		return UserMutationHelper.deleteById(
				new ResponseStudentDeleteById(), idStudent,
				repositoryStudent::deleteById,
				"Studiante Eliminado Correctamente");
	}
	
	public ResponseStudentUpdate update(String idStudent, RequestStudentUpdate request) {
		return UserMutationHelper.updateExisting(
				new ResponseStudentUpdate(),
				repositoryStudent.findById(idStudent),
				optional -> {
					EntityStudent entityStudent = optional.get();
					EntityUser entityUser = new EntityUser();

					EntityRole entityRole = new EntityRole();
					EntitySchool entitySchool = new EntitySchool();

					entityRole.setIdRole(request.getIdRole());
					entitySchool.setIdSchool(request.getIdSchool());

					entityUser.setFirstName(request.getFirstName());
					entityUser.setSurName(request.getSurName());
					entityUser.setEmail(request.getEmail());
					entityUser.setParentRole(entityRole);
					entityUser.setUpdatedAt(DateUtil.currentSqlDate());

					entityStudent.setCode(request.getCode());
					entityStudent.setTotalCredits(request.getTotalCredits());
					entityStudent.setParentSchool(entitySchool);
					entityStudent.setParentUser(entityUser);
					entityStudent.setUpdatedAt(DateUtil.currentSqlDate());

					repositoryUser.save(entityUser);
					repositoryStudent.save(entityStudent);
				},
				"Etudiante Actualizado Correctamente",
				"Error el Estudiante no se Actualizo");
	}
	
	public ResponseUserUpdatePassword updatePassword(String email, RequestUserUpdatePassword request) {
		return passwordUpdateHelper.updatePassword(
				email, request,
				"Contraseña de estudiante actualizada correctamente",
				"Contraseña de estudiante no actualizada");
	}
	
	public List<ResponseStudentSearch> searchStudentsForAutocomplete(String query, String idSchool) {
		if (query == null || query.trim().isEmpty() || idSchool == null || idSchool.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        List<EntityStudent> students = repositoryStudent.searchStudentsByTermAndSchool(query.trim(), idSchool.trim(), PageRequest.of(0, 10));
        
        return students.stream().map(student -> {
            String fullName = student.getParentUser().getFirstName() + " " + student.getParentUser().getSurName();
            return new ResponseStudentSearch(
                    student.getIdStudent(),
                    student.getCode(),
                    fullName
            );
        }).toList();
	}
	
	private String formatGrade(Double val) {
		if (val == null) return "-";
		if (val < 0) return "NSP";
		if (val == val.intValue()) {
			return String.valueOf(val.intValue());
		}
		return String.format(java.util.Locale.US, "%.1f", val);
	}
    


	private double calculateUnitPfValue(EntityUnitscore score, EntityGroup entityGroup) {
		double cc = GradeCalculationHelper.getRoundedScoreValue(score.getConceptualScore());
		double cp = GradeCalculationHelper.getRoundedScoreValue(score.getPracticalScore());
		double ca = GradeCalculationHelper.getRoundedScoreValue(score.getAttitudinalScore());

		return cc * entityGroup.getConceptualWeight() +
				cp * entityGroup.getPracticalWeight() +
				ca * entityGroup.getAttitudinalWeight();
	}

	private void processSingleUnitGrades(
			EntityUnits u,
			List<EntityUnitscore> savedScores,
			EntityGroup entityGroup,
			Map<String, Object> notasAlumno,
			double[] sumOfUnitScoresRef,
			boolean[] notasIncompletasRef) {
		
		EntityUnitscore score = GradeCalculationHelper.findScoreForUnit(savedScores, u);

		if (score == null) {
			notasIncompletasRef[0] = true;
			notasAlumno.put("CC" + u.getNumberUnit(), "-");
			notasAlumno.put("CP" + u.getNumberUnit(), "-");
			notasAlumno.put("CA" + u.getNumberUnit(), "-");
			notasAlumno.put("PF" + u.getNumberUnit(), "-");
			return;
		}

		String ccStr = formatGrade(score.getConceptualScore() != null ? (double) Math.round(score.getConceptualScore()) : null);
		String cpStr = formatGrade(score.getPracticalScore() != null ? (double) Math.round(score.getPracticalScore()) : null);
		String caStr = formatGrade(score.getAttitudinalScore() != null ? (double) Math.round(score.getAttitudinalScore()) : null);
		String pfStr = formatGrade((double) Math.round(score.getScore()));

		if (score.getConceptualScore() == null || score.getPracticalScore() == null || score.getAttitudinalScore() == null) {
			notasIncompletasRef[0] = true;
		} else {
			sumOfUnitScoresRef[0] += Math.round(calculateUnitPfValue(score, entityGroup));
		}

		notasAlumno.put("CC" + u.getNumberUnit(), ccStr);
		notasAlumno.put("CP" + u.getNumberUnit(), cpStr);
		notasAlumno.put("CA" + u.getNumberUnit(), caStr);
		notasAlumno.put("PF" + u.getNumberUnit(), pfStr);
	}

	private Map<String, Object> calculateUnitGrades(
			List<EntityUnits> units,
			List<EntityUnitscore> savedScores,
			EntityGroup entityGroup,
			double[] sumOfUnitScoresRef,
			boolean[] notasIncompletasRef) {
		
		Map<String, Object> notasAlumno = new HashMap<>();
		double[] sum = { 0.0 };
		boolean[] incomplete = { false };

		for (EntityUnits u : units) {
			processSingleUnitGrades(u, savedScores, entityGroup, notasAlumno, sum, incomplete);
		}

		sumOfUnitScoresRef[0] = sum[0];
		notasIncompletasRef[0] = incomplete[0];
		return notasAlumno;
	}


	private Map<String, Integer> calculateAttendanceSummary(List<EntityAttendance> attendances) {
		int countA = 0;
		int countT = 0;
		int countF = 0;
		int countJ = 0;

		for (EntityAttendance att : attendances) {
			if (att.getStatus() != null) {
				switch (att.getStatus().trim().toUpperCase()) {
					case "A": countA++; break;
					case "T": countT++; break;
					case "F": countF++; break;
					case "J": countJ++; break;
					default: break;
				}
			}
		}

		Map<String, Integer> resumenAsistencia = new HashMap<>();
		resumenAsistencia.put("A", countA);
		resumenAsistencia.put("T", countT);
		resumenAsistencia.put("F", countF);
		resumenAsistencia.put("J", countJ);
		return resumenAsistencia;
	}

	public Map<String, Object> getGradesByGroup(String idStudentKeycloak, String idGroup) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			EntityStudent entityStudent = repositoryStudent.findById(idStudentKeycloak)
					.orElseThrow(() -> new RuntimeException("El estudiante no ha sido encontrado en el sistema"));
			EntityGroup entityGroup = repositoryGroup.findById(idGroup)
					.orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
			
			EntityGroupStudent gs = repositoryGroupStudent.findByParentGroupAndParentStudent(entityGroup, entityStudent)
					.orElseThrow(() -> new RuntimeException("El estudiante no está matriculado en este grupo."));

			List<EntityUnits> units = repositoryUnits.findByParentGroupOrderByNumberUnitAsc(entityGroup);
			List<EntityAttendance> attendances = repositoryAttendance.findByParentGroupStudentIn(Arrays.asList(gs));

			return getGradesByGroupStudentPreloaded(gs, units, attendances);
			
		} catch (Exception e) {
			log.error("Error al procesar el registro", e);
			response.put(KEY_SUCCESS, false);
			response.put(KEY_ERROR, "Error al procesar el registro: " + e.getMessage());
		}
		
		return response;
	}
	
	public Map<String, Object> getGradesByGroupStudentPreloaded(
			EntityGroupStudent gs, 
			List<EntityUnits> units, 
			List<EntityAttendance> attendances) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			EntityGroup entityGroup = gs.getParentGroup();
			List<EntityUnitscore> savedScores = gs.getChildUnitscore() != null ? gs.getChildUnitscore() : new ArrayList<>();
			
			double[] sumOfUnitScoresRef = new double[1];
			boolean[] notasIncompletasRef = new boolean[1];
			Map<String, Object> notasAlumno = calculateUnitGrades(units, savedScores, entityGroup, sumOfUnitScoresRef, notasIncompletasRef);
			
			double sumOfUnitScores = sumOfUnitScoresRef[0];
			boolean notasIncompletas = notasIncompletasRef[0];

			notasAlumno.put("PPF", GradeCalculationHelper.calculatePpfLabel(sumOfUnitScores, units.size(), notasIncompletas));

			response.put("notas", notasAlumno);

			Map<String, Integer> resumenAsistencia = calculateAttendanceSummary(attendances);

			response.put("asistencia", resumenAsistencia);
			response.put(KEY_SUCCESS, true);
			
		} catch (Exception e) {
			log.error("Error al procesar el registro", e);
			response.put(KEY_SUCCESS, false);
			response.put(KEY_ERROR, "Error al procesar el registro: " + e.getMessage());
		}
		
		return response;
	}

	private void populateBoletaCourseInfo(Map<String, Object> boleta, EntityGroup grupo) {
		EntityCourse course = grupo.getParentCourse();
		String nombreCurso = "Curso sin nombre";
		String codigoCurso = "N/A";
		int creditos = 0;
		String categoria = "N/A";

		if (course != null) {
			nombreCurso = course.getNameCourse() != null ? course.getNameCourse() : "Curso sin nombre";
			codigoCurso = course.getCode() != null ? course.getCode() : "N/A";
			creditos = course.getCredits();
			categoria = course.getCategory() != null ? course.getCategory() : "N/A";
		}

		boleta.put("nombreCurso", nombreCurso);
		boleta.put("codigoCurso", codigoCurso);
		boleta.put("creditos", creditos);
		boleta.put("categoria", categoria);
		boleta.put("seccion", grupo.getNameGroup());
		boleta.put("idGroup", grupo.getIdGroup());
	}

	public Map<String, Object> getAllBoletasForStudent(String idStudentKeycloak) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> listaBoletas = new ArrayList<>();

        try {
            List<EntityGroupStudent> enrollments = repositoryGroupStudent.findEnrollmentsWithGroupAndScoresByStudentId(idStudentKeycloak);

            if (enrollments == null || enrollments.isEmpty()) {
                response.put(KEY_SUCCESS, true);
                response.put(KEY_MESSAGE, "No estás matriculado en ningún curso este semestre.");
                response.put("data", listaBoletas);
                return response;
            }

            List<EntityGroup> groups = enrollments.stream().map(EntityGroupStudent::getParentGroup).toList();
            List<EntityUnits> allUnits = repositoryUnits.findByParentGroupInOrderByNumberUnitAsc(groups);
            List<EntityAttendance> allAttendances = repositoryAttendance.findByParentGroupStudentIn(enrollments);

            // Group units by idGroup
            Map<String, List<EntityUnits>> unitsByGroup = allUnits.stream()
                .collect(Collectors.groupingBy(u -> u.getParentGroup().getIdGroup()));

            // Group attendances by idGroupStudent
            Map<String, List<EntityAttendance>> attendancesByGroupStudent = allAttendances.stream()
                .collect(Collectors.groupingBy(a -> a.getParentGroupStudent().getIdGroupStudent()));

            for (EntityGroupStudent enrollment : enrollments) {
                EntityGroup grupo = enrollment.getParentGroup();
                List<EntityUnits> units = unitsByGroup.getOrDefault(grupo.getIdGroup(), new ArrayList<>());
                List<EntityAttendance> attendances = attendancesByGroupStudent.getOrDefault(enrollment.getIdGroupStudent(), new ArrayList<>());

                Map<String, Object> boletaDelCurso = this.getGradesByGroupStudentPreloaded(enrollment, units, attendances);

                if (!boletaDelCurso.containsKey(KEY_ERROR)) {
                    populateBoletaCourseInfo(boletaDelCurso, grupo);
                    listaBoletas.add(boletaDelCurso);
                } else {
                    log.warn("Aviso: {} en el grupo {}", boletaDelCurso.get(KEY_ERROR), grupo.getNameGroup());
                }
            }

            response.put(KEY_SUCCESS, true);
            response.put("data", listaBoletas);

        } catch (Exception e) {
            log.error("Error al cargar las boletas del semestre", e);
            response.put(KEY_SUCCESS, false);
            response.put(KEY_ERROR, "Error al cargar las boletas del semestre: " + e.getMessage());
        }

        return response;
    }
}
