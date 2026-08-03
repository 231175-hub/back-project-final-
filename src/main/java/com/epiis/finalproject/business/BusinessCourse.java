package com.epiis.finalproject.business;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
import com.epiis.finalproject.helper.DateUtil;
import com.epiis.finalproject.helper.ResponseMapBuilder;
import com.epiis.finalproject.repository.RepositoryCourse;

@Service
public class BusinessCourse {
	private final RepositoryCourse repositoryCourse;

	public BusinessCourse(RepositoryCourse repositoryCourse) {
		this.repositoryCourse = repositoryCourse;
	}

	public ResponseCourseInsert insert(RequestCourseInsert request) {
		ResponseCourseInsert response = new ResponseCourseInsert();

		EntitySchool entitySchool = new EntitySchool();
		entitySchool.setIdSchool(request.getIdSchool());

		EntityCourse entityCourse = new EntityCourse();
		entityCourse.setIdCourse(UUID.randomUUID().toString());
		entityCourse.setCode(request.getCode());
		entityCourse.setNameCourse(request.getNameCourse());
		entityCourse.setCategory(request.getCategory());
		entityCourse.setCredits(request.getCredits());
		entityCourse.setParentSchool(entitySchool);
		entityCourse.setCreatedAt(DateUtil.currentSqlDate());
		entityCourse.setUpdatedAt(entityCourse.getCreatedAt());

		repositoryCourse.save(entityCourse);

		response.success();
		response.getListMessage().add("Curso Registrado Exitosamente");
		return response;
	}

	public Map<String, Object> getAll() {
		return ResponseMapBuilder.buildDataMap(
				new ResponseCourseGetAll(),
				"Cursos Entregados Correctamente",
				repositoryCourse.findAll());
	}

	public Map<String, Object> getById(String idCourse) {
		return ResponseMapBuilder.buildDataMap(
				new ResponseCourseGetById(),
				"Curso Encontrado Correctamente",
				repositoryCourse.findById(idCourse));
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
			entityCourse.setCategory(request.getCategory());
			entityCourse.setParentSchool(entitySchool);
			entityCourse.setUpdatedAt(DateUtil.currentSqlDate());

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
		if (idSchool == null || idSchool.trim().isEmpty()) {
			return Collections.emptyList();
		}

		String term = query == null ? "" : query.trim();

		List<EntityCourse> courses = repositoryCourse.searchCoursesByTermAndSchool(term, idSchool.trim(),
				PageRequest.of(0, 10));

		return courses.stream().map(course -> new ResponseCourseSearch(
				course.getIdCourse(),
				course.getCode(),
				course.getNameCourse(),
				course.getCredits(),
				course.getCategory())).toList();
	}
}
