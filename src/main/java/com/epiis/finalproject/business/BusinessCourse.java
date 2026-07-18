package com.epiis.finalproject.business;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.epiis.finalproject.dto.request.course.RequestCourseInsert;
import com.epiis.finalproject.dto.request.course.RequestCourseUpdate;
import com.epiis.finalproject.dto.response.course.ResponseCourseDeleteById;
import com.epiis.finalproject.dto.response.course.ResponseCourseGetAll;
import com.epiis.finalproject.dto.response.course.ResponseCourseGetById;
import com.epiis.finalproject.dto.response.course.ResponseCourseInsert;
import com.epiis.finalproject.dto.response.course.ResponseCourseSearch;
import com.epiis.finalproject.dto.response.course.ResponseCourseUpdate;
import com.epiis.finalproject.entity.EntityCourse;
import com.epiis.finalproject.entity.EntitySchool;

import com.epiis.finalproject.repository.RepositoryCourse;

@Service

public class BusinessCourse {
	private final RepositoryCourse repositoryCourse;

	public BusinessCourse(RepositoryCourse repositoryCourse) {
		this.repositoryCourse = repositoryCourse;
	}

	public ResponseCourseInsert insert(RequestCourseInsert request) {
		ResponseCourseInsert response = new ResponseCourseInsert();

		EntityCourse entityCourse = new EntityCourse();

		EntitySchool entitySchool = new EntitySchool();

		entitySchool.setIdSchool(request.getIdSchool());

		entityCourse.setIdCourse(UUID.randomUUID().toString());
		entityCourse.setCode(request.getCode());
		entityCourse.setNameCourse(request.getNameCourse());
		entityCourse.setCategory(request.getCategory());
		entityCourse.setCredits(request.getCredits());
		entityCourse.setParentSchool(entitySchool);
		entityCourse.setCreatedAt(new java.sql.Date(new Date().getTime()));
		entityCourse.setUpdatedAt(entityCourse.getCreatedAt());

		repositoryCourse.save(entityCourse);

		response.success();
		response.getListMessage().add("Curso Registrado Exitosamente");

		return response;
	}

	public Map<String, Object> getAll() {
		ResponseCourseGetAll response = new ResponseCourseGetAll();

		Map<String, Object> res = new HashMap<>();

		List<EntityCourse> entityCourse = repositoryCourse.findAll();

		response.success();
		response.getListMessage().add("Cursos Entregados Correctamente");

		res.put("message", response);
		res.put("data", entityCourse);

		return res;
	}

	public Map<String, Object> getById(String idCourse) {
		ResponseCourseGetById response = new ResponseCourseGetById();

		Map<String, Object> res = new HashMap<>();

		Optional<EntityCourse> entityCourse = repositoryCourse.findById(idCourse);

		response.success();
		response.getListMessage().add("Curso Encontrado Correctamente");

		res.put("message", response);
		res.put("data", entityCourse);

		return res;
	}

	public ResponseCourseDeleteById deleteById(String idCourse) {
		ResponseCourseDeleteById response = new ResponseCourseDeleteById();

		repositoryCourse.deleteById(idCourse);

		response.success();
		response.getListMessage().add("Curso Eliminado Correctamente");

		return response;
	}

	public ResponseCourseUpdate update(String idCourse, RequestCourseUpdate request) {
		ResponseCourseUpdate response = new ResponseCourseUpdate();

		Optional<EntityCourse> optional = repositoryCourse.findById(idCourse);

		if (optional.isPresent()) {
			EntityCourse entityCourse = optional.get();

			EntitySchool entitySchool = new EntitySchool();

			entitySchool.setIdSchool(request.getIdSchool());

			entityCourse.setCode(request.getCode());
			entityCourse.setCredits(request.getCredits());
			entityCourse.setNameCourse(request.getNameCourse());
			entityCourse.setParentSchool(entitySchool);
			entityCourse.setUpdatedAt(new java.sql.Date(new Date().getTime()));

			repositoryCourse.save(entityCourse);

			response.success();
			response.getListMessage().add("Curso Actualizado Exitosamente");

			return response;
		}

		response.error();
		response.getListMessage().add("Error el Curso no se Actualizo");

		return response;
	}

	public List<ResponseCourseSearch> searchCoursesForAutocomplete(String query, String idSchool) {
		if (query == null || query.trim().isEmpty() || idSchool == null || idSchool.trim().isEmpty()) {
			return Collections.emptyList();
		}

		List<EntityCourse> courses = repositoryCourse.searchCoursesByTermAndSchool(query.trim(), idSchool.trim(),
				PageRequest.of(0, 10));

		return courses.stream().map(course -> new ResponseCourseSearch(
				course.getIdCourse(),
				course.getCode(),
				course.getNameCourse())).collect(Collectors.toList());
	}
}
