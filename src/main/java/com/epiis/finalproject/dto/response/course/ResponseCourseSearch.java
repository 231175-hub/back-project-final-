package com.epiis.finalproject.dto.response.course;

public record ResponseCourseSearch(
    String idCourse, 
    String code, 
    String nameCourse,
    int credits,
    String category
) {}