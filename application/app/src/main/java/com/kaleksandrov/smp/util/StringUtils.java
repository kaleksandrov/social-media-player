package com.kaleksandrov.smp.util;

/**
 * Holds a bunch of static methods for manipulating {@link String} objects.
 *
 * @author kaleksandrov
 */
public class StringUtils {

    /**
     * Prevent outer instantiation and inheritance.
     */
    private StringUtils() {
        // nothing
    }

    /**
     * Non-null empty {@link String} value.
     */
    public static final String EMPTY = "";

    /**
     * Checks if the given value is not null and has length grater than 0.
     *
     * @param value The {@link String} to be checked.
     * @return <code>true</code> if the value is not null and has length grater than 0, otherwise -
     * <code>false</code>
     */
    public static boolean isEmpty(String value) {
        return (value == null || value.isEmpty());
    }
}
