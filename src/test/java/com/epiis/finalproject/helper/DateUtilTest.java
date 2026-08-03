package com.epiis.finalproject.helper;

import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    @Test
    void testCurrentSqlDateIsNotNull() {
        Date result = DateUtil.currentSqlDate();
        assertNotNull(result);
    }

    @Test
    void testCurrentSqlDateIsCloseToNow() {
        long before = System.currentTimeMillis();
        Date result = DateUtil.currentSqlDate();
        long after = System.currentTimeMillis();

        assertTrue(result.getTime() >= before);
        assertTrue(result.getTime() <= after);
    }

    @Test
    void testCurrentSqlDateReturnsSqlDate() {
        Date result = DateUtil.currentSqlDate();
        assertInstanceOf(java.sql.Date.class, result);
    }
}
