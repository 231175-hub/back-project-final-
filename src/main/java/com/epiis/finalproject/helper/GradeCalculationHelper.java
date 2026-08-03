package com.epiis.finalproject.helper;

import com.epiis.finalproject.entity.EntityUnits;
import com.epiis.finalproject.entity.EntityUnitscore;

import java.util.List;

/**
 * Utility class that centralizes grade calculation logic
 * shared across BusinessStudent and BusinessGroupRegister.
 */
public class GradeCalculationHelper {

    private GradeCalculationHelper() {
        // Utility class — prevent instantiation
    }

    /**
     * Returns the rounded score value, treating negative scores as 0.0 (NSP).
     */
    public static double getRoundedScoreValue(Double val) {
        return val < 0 ? 0.0 : Math.round(val);
    }

    /**
     * Finds the EntityUnitscore that corresponds to a given EntityUnits
     * within a list of saved scores.
     *
     * @return the matching EntityUnitscore, or null if not found
     */
    public static EntityUnitscore findScoreForUnit(List<EntityUnitscore> savedScores, EntityUnits unit) {
        for (EntityUnitscore s : savedScores) {
            if (s.getParentUnits().getIdUnits().equals(unit.getIdUnits())) {
                return s;
            }
        }
        return null;
    }

    /**
     * Calculates the PPF label string based on the sum of unit scores,
     * the unit count, and whether any grades are incomplete.
     *
     * @param sumOfUnitScores the accumulated sum of rounded unit final scores
     * @param unitCount       the number of academic units
     * @param notasIncompletas true if any component grades are missing
     * @return the PPF display value ("-", "NSP", or the rounded numeric string)
     */
    public static String calculatePpfLabel(double sumOfUnitScores, int unitCount, boolean notasIncompletas) {
        if (notasIncompletas) {
            return "-";
        }
        double rawPpf = sumOfUnitScores / (unitCount == 0 ? 1 : unitCount);
        if (rawPpf == 0) {
            return "NSP";
        }
        return String.valueOf(Math.round(rawPpf));
    }
}
