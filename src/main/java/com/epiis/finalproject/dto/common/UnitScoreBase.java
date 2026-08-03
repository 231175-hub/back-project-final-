package com.epiis.finalproject.dto.common;

import lombok.Getter;
import lombok.Setter;

/**
 * Shared unit-score fields used by the group register request and response DTOs
 * to avoid duplicating the score columns in both nested structures.
 */
@Getter
@Setter
public abstract class UnitScoreBase {
    private Double conceptualScore;
    private Double practicalScore;
    private Double test1Score;
    private Double test2Score;
    private Double attitudinalScore;
    private String conceptualGrades;
    private String practicalGrades;
    private String testGrades;
    private double score;
}
