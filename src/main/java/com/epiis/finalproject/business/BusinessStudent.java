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
import com.epiis.finalproject.entity.EntityGroup;
import com.epiis.finalproject.entity.EntityGroupStudent;
import com.epiis.finalproject.entity.EntityRole;
import com.epiis.finalproject.entity.EntitySchool;
import com.epiis.finalproject.entity.EntityStudent;
import com.epiis.finalproject.entity.EntityUnits;
import com.epiis.finalproject.entity.EntityUnitscore;
import com.epiis.finalproject.entity.EntityUser;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.epiis.finalproject.entity.EntityGroup;
import com.epiis.finalproject.entity.EntityGroupStudent;
import com.epiis.finalproject.entity.EntityRole;
import com.epiis.finalproject.entity.EntitySchool;
import com.epiis.finalproject.entity.EntityStudent;
import com.epiis.finalproject.entity.EntityUnits;
import com.epiis.finalproject.entity.EntityUnitscore;
import com.epiis.finalproject.entity.EntityUser;
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

import jakarta.transaction.Transactional;

@Service
public class BusinessStudent {
	private final RepositoryStudent repositoryStudent;
	private final RepositoryRole repositoryRole;
	private final RepositorySchool repositorySchool;
	private final RepositoryUser repositoryUser;
	private final PasswordEncoder passwordEncoder;
	private final RepositoryGroup repositoryGroup;
	private final RepositoryGroupStudent repositoryGroupStudent;
	private final RepositoryUnits repositoryUnits;
	private final RepositoryAttendance repositoryAttendance;
	
	public BusinessStudent(
			RepositoryStudent repositoryStudent, 
			RepositoryUser repositoryUser, 
			RepositoryRole repositoryRole, 
			RepositorySchool repositorySchool, 
			PasswordEncoder passwordEncoder, 
			RepositoryGroup repositoryGroup, 
			RepositoryGroupStudent repositoryGroupStudent,
			RepositoryUnits repositoryUnits,
			RepositoryAttendance repositoryAttendance) {
		this.repositoryStudent = repositoryStudent;
		this.repositoryUser = repositoryUser;
		this.repositoryRole = repositoryRole;
		this.repositorySchool = repositorySchool;
		this.passwordEncoder = passwordEncoder;
		this.repositoryGroup = repositoryGroup;
		this.repositoryGroupStudent = repositoryGroupStudent;
		this.repositoryUnits = repositoryUnits;
		this.repositoryAttendance = repositoryAttendance;
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

	        String userId = UUID.randomUUID().toString();
	    
	        EntityUser entityUser = new EntityUser();
	        entityUser.setIdUser(userId); 
	        entityUser.setFirstName(request.getFirstName());
	        entityUser.setSurName(request.getSurName());
	        entityUser.setEmail(request.getEmail());
	        entityUser.setPassword(passwordEncoder.encode(request.getPassword()));
	        
	        entityUser.setParentRole(roleStudent);
	        entityUser.setCreatedAt(new java.sql.Date(new Date().getTime()));
	        entityUser.setUpdatedAt(entityUser.getCreatedAt());
	        
	        repositoryUser.save(entityUser);
	        
	        EntityStudent entityStudent = new EntityStudent();
	        entityStudent.setIdStudent(userId); 
	        entityStudent.setCode(request.getCode());
	        entityStudent.setTotalCredits(request.getTotalCredits());
	        entityStudent.setStatus(EnumStudent.ACTIVE.toString());
	        
	        entityStudent.setParentUser(entityUser);
	        entityStudent.setParentSchool(entitySchool); 
	        
	        entityStudent.setCreatedAt(entityUser.getCreatedAt());
	        entityStudent.setUpdatedAt(entityUser.getCreatedAt());

	        repositoryStudent.save(entityStudent);
	       
	        response.success();
	        response.getListMessage().add("Estudiante registrado correctamente.");
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.error();
	        response.getListMessage().add("Error al registrar el estudiante: " + e.getMessage());
	    }
	    
	    return response;
	}
	
	public Map<String, Object> getAll(){
		ResponseStudentGetAll response = new ResponseStudentGetAll();
		Map<String, Object> res = new HashMap<>();
		List<EntityStudent> entityStudent = repositoryStudent.studentsWithSchoolAndUser();
		
		response.success();
		response.getListMessage().add("Estudiantes Entregados exitosamente");
		res.put("message", response);
		res.put("data", entityStudent);
		return res;
	}
	
	public Map<String, Object> getById(String idStudent){
		ResponseStudentGetById response = new ResponseStudentGetById();
		Map<String, Object> res = new HashMap<>();
		Optional<EntityStudent> entityStudent = repositoryStudent.findByIdWithSchoolAndUser(idStudent);
		
		response.success();
		response.getListMessage().add("Estudiante Encontrado exitosamente");
		res.put("message", response);
		
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
		ResponseStudentDeleteById response = new ResponseStudentDeleteById();
		repositoryStudent.deleteById(idStudent);
		response.success();
		response.getListMessage().add("Studiante Eliminado Correctamente");
		return response;
	}
	
	public ResponseStudentUpdate update(String idStudent, RequestStudentUpdate request) {
		ResponseStudentUpdate response = new ResponseStudentUpdate();
		Optional<EntityStudent> optional = repositoryStudent.findById(idStudent);
		
		if (optional.isPresent()) {
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
			entityUser.setUpdatedAt(new java.sql.Date(new Date().getTime()));
			
			entityStudent.setCode(request.getCode());
			entityStudent.setTotalCredits(request.getTotalCredits());
			entityStudent.setParentSchool(entitySchool);
			entityStudent.setParentUser(entityUser);
			entityStudent.setUpdatedAt(new java.sql.Date(new Date().getTime()));
			
			repositoryUser.save(entityUser);
			repositoryStudent.save(entityStudent);
			
			response.success();
			response.getListMessage().add("Etudiante Actualizado Correctamente");
			return response;
		}
		
		response.error();
		response.getListMessage().add("Error el Estudiante no se Actualizo");
		return response;
	}
	
	public ResponseUserUpdatePassword updatePassword(String email, RequestUserUpdatePassword request) {
		ResponseUserUpdatePassword response = new ResponseUserUpdatePassword();
		Optional<EntityUser> optional = repositoryUser.findByEmail(email);
		
		if (optional.isPresent()) {
			EntityUser entityUser = optional.get();
			entityUser.setUpdatedAt(new java.sql.Date(new Date().getTime()));
			response.success();
			response.getListMessage().add("Contraseña de estudiante actualizada correctamente");
		} else {
		    response.error();
		    response.getListMessage().add("Contraseña de estudiante no actualizada");
        }
		return response;
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
        }).collect(Collectors.toList());
	}
	
	private String formatGrade(Double val) {
		if (val == null) return "-";
		if (val < 0) return "NSP";
		if (val == val.intValue()) {
			return String.valueOf(val.intValue());
		}
		return String.format(java.util.Locale.US, "%.1f", val);
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

			List<EntityUnitscore> savedScores = gs.getChildUnitscore() != null ? gs.getChildUnitscore() : new ArrayList<>();
			List<EntityUnits> units = repositoryUnits.findByParentGroupOrderByNumberUnitAsc(entityGroup);
			
			Map<String, Object> notasAlumno = new HashMap<>();
			boolean notasIncompletas = false;
			double sumOfUnitScores = 0;

			for (EntityUnits u : units) {
				EntityUnitscore score = null;
				for (EntityUnitscore s : savedScores) {
					if (s.getParentUnits().getIdUnits().equals(u.getIdUnits())) {
						score = s;
						break;
					}
				}

				String ccStr = "-";
				String cpStr = "-";
				String caStr = "-";
				String pfStr = "-";

				if (score != null) {
					ccStr = formatGrade(score.getConceptualScore() != null ? (double) Math.round(score.getConceptualScore()) : null);
					cpStr = formatGrade(score.getPracticalScore() != null ? (double) Math.round(score.getPracticalScore()) : null);
					caStr = formatGrade(score.getAttitudinalScore() != null ? (double) Math.round(score.getAttitudinalScore()) : null);
					pfStr = formatGrade((double) Math.round(score.getScore()));

					if (score.getConceptualScore() == null || score.getPracticalScore() == null || score.getAttitudinalScore() == null) {
						notasIncompletas = true;
					} else {
						// Treat NSP (-1.0) as 0.0 for calculations, and round component grades
						double cc = score.getConceptualScore() < 0 ? 0.0 : Math.round(score.getConceptualScore());
						double cp = score.getPracticalScore() < 0 ? 0.0 : Math.round(score.getPracticalScore());
						double ca = score.getAttitudinalScore() < 0 ? 0.0 : Math.round(score.getAttitudinalScore());

						double unitPf = cc * entityGroup.getConceptualWeight() +
										cp * entityGroup.getPracticalWeight() +
										ca * entityGroup.getAttitudinalWeight();
						sumOfUnitScores += Math.round(unitPf);
					}
				} else {
					notasIncompletas = true;
				}

				notasAlumno.put("CC" + u.getNumberUnit(), ccStr);
				notasAlumno.put("CP" + u.getNumberUnit(), cpStr);
				notasAlumno.put("CA" + u.getNumberUnit(), caStr);
				notasAlumno.put("PF" + u.getNumberUnit(), pfStr);
			}

			if (notasIncompletas) {
				notasAlumno.put("PPF", "-");
			} else {
				double rawPpf = sumOfUnitScores / units.size();
				if (rawPpf == 0) {
					notasAlumno.put("PPF", "NSP");
				} else {
					notasAlumno.put("PPF", String.valueOf(Math.round(rawPpf)));
				}
			}

			response.put("notas", notasAlumno);

			List<EntityAttendance> attendances = repositoryAttendance.findByParentGroupStudentIn(Arrays.asList(gs));
			int countA = 0, countT = 0, countF = 0, countJ = 0;

			for (EntityAttendance att : attendances) {
				if (att.getStatus() != null) {
					switch (att.getStatus().trim().toUpperCase()) {
						case "A": countA++; break;
						case "T": countT++; break;
						case "F": countF++; break;
						case "J": countJ++; break;
					}
				}
			}

			Map<String, Integer> resumenAsistencia = new HashMap<>();
			resumenAsistencia.put("A", countA);
			resumenAsistencia.put("T", countT);
			resumenAsistencia.put("F", countF);
			resumenAsistencia.put("J", countJ);

			response.put("asistencia", resumenAsistencia);
			response.put("success", true);
			
		} catch (Exception e) {
			e.printStackTrace();
			response.put("error", "Error al procesar el registro: " + e.getMessage());
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
			
			Map<String, Object> notasAlumno = new HashMap<>();
			boolean notasIncompletas = false;
			double sumOfUnitScores = 0;

			for (EntityUnits u : units) {
				EntityUnitscore score = null;
				for (EntityUnitscore s : savedScores) {
					if (s.getParentUnits().getIdUnits().equals(u.getIdUnits())) {
						score = s;
						break;
					}
				}

				String ccStr = "-";
				String cpStr = "-";
				String caStr = "-";
				String pfStr = "-";

				if (score != null) {
					ccStr = formatGrade(score.getConceptualScore() != null ? (double) Math.round(score.getConceptualScore()) : null);
					cpStr = formatGrade(score.getPracticalScore() != null ? (double) Math.round(score.getPracticalScore()) : null);
					caStr = formatGrade(score.getAttitudinalScore() != null ? (double) Math.round(score.getAttitudinalScore()) : null);
					pfStr = formatGrade((double) Math.round(score.getScore()));

					if (score.getConceptualScore() == null || score.getPracticalScore() == null || score.getAttitudinalScore() == null) {
						notasIncompletas = true;
					} else {
						double cc = score.getConceptualScore() < 0 ? 0.0 : Math.round(score.getConceptualScore());
						double cp = score.getPracticalScore() < 0 ? 0.0 : Math.round(score.getPracticalScore());
						double ca = score.getAttitudinalScore() < 0 ? 0.0 : Math.round(score.getAttitudinalScore());

						double unitPf = cc * entityGroup.getConceptualWeight() +
										cp * entityGroup.getPracticalWeight() +
										ca * entityGroup.getAttitudinalWeight();
						sumOfUnitScores += Math.round(unitPf);
					}
				} else {
					notasIncompletas = true;
				}

				notasAlumno.put("CC" + u.getNumberUnit(), ccStr);
				notasAlumno.put("CP" + u.getNumberUnit(), cpStr);
				notasAlumno.put("CA" + u.getNumberUnit(), caStr);
				notasAlumno.put("PF" + u.getNumberUnit(), pfStr);
			}

			if (notasIncompletas) {
				notasAlumno.put("PPF", "-");
			} else {
				double rawPpf = sumOfUnitScores / (units.isEmpty() ? 1 : units.size());
				if (rawPpf == 0) {
					notasAlumno.put("PPF", "NSP");
				} else {
					notasAlumno.put("PPF", String.valueOf(Math.round(rawPpf)));
				}
			}

			response.put("notas", notasAlumno);

			int countA = 0, countT = 0, countF = 0, countJ = 0;
			for (EntityAttendance att : attendances) {
				if (att.getStatus() != null) {
					switch (att.getStatus().trim().toUpperCase()) {
						case "A": countA++; break;
						case "T": countT++; break;
						case "F": countF++; break;
						case "J": countJ++; break;
					}
				}
			}

			Map<String, Integer> resumenAsistencia = new HashMap<>();
			resumenAsistencia.put("A", countA);
			resumenAsistencia.put("T", countT);
			resumenAsistencia.put("F", countF);
			resumenAsistencia.put("J", countJ);

			response.put("asistencia", resumenAsistencia);
			response.put("success", true);
			
		} catch (Exception e) {
			e.printStackTrace();
			response.put("error", "Error al procesar el registro: " + e.getMessage());
		}
		
		return response;
	}

	public Map<String, Object> getAllBoletasForStudent(String idStudentKeycloak) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> listaBoletas = new ArrayList<>();

        try {
            List<EntityGroupStudent> enrollments = repositoryGroupStudent.findEnrollmentsWithGroupAndScoresByStudentId(idStudentKeycloak);

            if (enrollments == null || enrollments.isEmpty()) {
                response.put("success", true);
                response.put("message", "No estás matriculado en ningún curso este semestre.");
                response.put("data", listaBoletas);
                return response;
            }

            List<EntityGroup> groups = enrollments.stream().map(EntityGroupStudent::getParentGroup).collect(Collectors.toList());
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

                if (!boletaDelCurso.containsKey("error")) {
                    String nombreCurso = (grupo.getParentCourse() != null) ? grupo.getParentCourse().getNameCourse() : "Curso sin nombre";
                    String codigoCurso = (grupo.getParentCourse() != null) ? grupo.getParentCourse().getCode() : "N/A";
                    int creditos = (grupo.getParentCourse() != null) ? grupo.getParentCourse().getCredits() : 0;
                    String categoria = (grupo.getParentCourse() != null) ? grupo.getParentCourse().getCategory() : "N/A";

                    boletaDelCurso.put("nombreCurso", nombreCurso);
                    boletaDelCurso.put("codigoCurso", codigoCurso);
                    boletaDelCurso.put("creditos", creditos);
                    boletaDelCurso.put("categoria", categoria);
                    boletaDelCurso.put("seccion", grupo.getNameGroup());
                    boletaDelCurso.put("idGroup", grupo.getIdGroup());
                    
                    listaBoletas.add(boletaDelCurso);
                } else {
                    System.out.println("Aviso: " + boletaDelCurso.get("error") + " en el grupo " + grupo.getNameGroup());
                }
            }

            response.put("success", true);
            response.put("data", listaBoletas);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("error", "Error al cargar las boletas del semestre: " + e.getMessage());
        }

        return response;
    }
}
