package org.libmanager.server.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {

    /** The date pattern used to format */
    private static final String DATE_PATTERN = "dd/MM/yyyy";

    /** The date pattern used to format for the db */
    private static final String DB_DATE_PATTERN = "yyyy-MM-dd";

    /** The date formatter */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    /** The date formatter for the db */
    private static final DateTimeFormatter DB_DATE_FORMATTER = DateTimeFormatter.ofPattern(DB_DATE_PATTERN);

    /**
     * Returns the given date as a formatted string
     * The format used is {@link DateUtil#DATE_PATTERN}.
     *
     * @param date
     *          The date to be returned as a string
     * @return
     *          The formatted string
     */
    public static String format(LocalDate date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMATTER.format(date);
    }

    /**
     * Returns the given date as a formatted string for the database
     * The format used is {@link DateUtil#DB_DATE_PATTERN}.
     *
     * @param date
     *          The date to be returned as a string
     * @return
     *          The formatted string
     */
    public static String formatDB(LocalDate date) {
        if (date == null) {
            return null;
        }
        return DB_DATE_FORMATTER.format(date);
    }

    /**
     * Converts a String in the format of the defined {@link DateUtil#DATE_PATTERN}
     * to a {@link LocalDate} object.
     *
     * Returns null if the String could not be converted
     *
     * @param dateString
     *          The date as String
     * @return
     *          The date object or null if it could not be converted
     */
    public static LocalDate parse(String dateString) {
        try {
            return DATE_FORMATTER.parse(dateString, LocalDate::from);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Converts a String in the format of the defined {@link DateUtil#DB_DATE_PATTERN}
     * to a {@link LocalDate} object.
     *
     * Returns null if the String could not be converted
     *
     * @param dateString
     *          The date as String from db
     * @return
     *          The date object or null if it could not be converted
     */
    public static LocalDate parseDB(String dateString) {
        try {
            return DB_DATE_FORMATTER.parse(dateString, LocalDate::from);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Checks the String whether it is a valid date
     * @param dateString
     *          The string to check
     * @return
     *          True if <code>dateString</code> is a valid date
     */
    public static boolean validDate(String dateString) {
        return DateUtil.parse(dateString) != null;
    }

}
