package com.epiis.finalproject.helper;

import com.epiis.finalproject.entity.EntityUnits;
import com.epiis.finalproject.entity.EntityUnitscore;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GradeCalculationHelperTest {

    @Test
    void testGetRoundedScoreValueNegative() {
        assertEquals(0.0, GradeCalculationHelper.getRoundedScoreValue(-1.0));
    }

    @Test
    void testGetRoundedScoreValuePositive() {
        assertEquals(16.0, GradeCalculationHelper.getRoundedScoreValue(15.6));
        assertEquals(14.0, GradeCalculationHelper.getRoundedScoreValue(14.4));
        assertEquals(0.0, GradeCalculationHelper.getRoundedScoreValue(0.0));
    }

    @Test
    void testFindScoreForUnitFound() {
        EntityUnits unit = new EntityUnits();
        unit.setIdUnits("u1");

        EntityUnitscore score = new EntityUnitscore();
        score.setParentUnits(unit);

        List<EntityUnitscore> scores = List.of(score);

        EntityUnitscore found = GradeCalculationHelper.findScoreForUnit(scores, unit);
        assertNotNull(found);
        assertSame(score, found);
    }

    @Test
    void testFindScoreForUnitNotFound() {
        EntityUnits unit1 = new EntityUnits();
        unit1.setIdUnits("u1");

        EntityUnits unit2 = new EntityUnits();
        unit2.setIdUnits("u2");

        EntityUnitscore score = new EntityUnitscore();
        score.setParentUnits(unit1);

        List<EntityUnitscore> scores = List.of(score);

        EntityUnitscore found = GradeCalculationHelper.findScoreForUnit(scores, unit2);
        assertNull(found);
    }

    @Test
    void testFindScoreForUnitEmptyList() {
        EntityUnits unit = new EntityUnits();
        unit.setIdUnits("u1");

        assertNull(GradeCalculationHelper.findScoreForUnit(new ArrayList<>(), unit));
    }

    @Test
    void testCalculatePpfLabelIncomplete() {
        assertEquals("-", GradeCalculationHelper.calculatePpfLabel(45.0, 3, true));
    }

    @Test
    void testCalculatePpfLabelNSP() {
        assertEquals("NSP", GradeCalculationHelper.calculatePpfLabel(0.0, 3, false));
    }

    @Test
    void testCalculatePpfLabelNormal() {
        assertEquals("15", GradeCalculationHelper.calculatePpfLabel(45.0, 3, false));
    }

    @Test
    void testCalculatePpfLabelZeroUnits() {
        // Should not divide by zero; uses 1 instead
        assertEquals("15", GradeCalculationHelper.calculatePpfLabel(15.0, 0, false));
    }
}
