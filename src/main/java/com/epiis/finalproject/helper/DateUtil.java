package com.epiis.finalproject.helper;

import java.util.Date;

/**
 * Utility class to avoid repeated new java.sql.Date(new Date().getTime()) across Business classes.
 */
public final class DateUtil {

    private DateUtil() {
        // utility class
    }

    /**
     * Returns the current instant as a {@link java.sql.Date}.
     */
    public static java.sql.Date currentSqlDate() {
        return new java.sql.Date(new Date().getTime());
    }
}
