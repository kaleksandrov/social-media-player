package com.kaleksandrov.smp.util;

import android.util.Log;

/**
 * Holds a bunch of static methods for validation.
 * 
 * @author kaleksandrov
 * 
 */
public class ValidationUtils {

	/**
	 * Prevent outer instantiation and inheritance.
	 */
	private ValidationUtils() {
		// nothing
	}

	/**
	 * Validates if the given objects is not null. If it is not, throws an
	 * {@link IllegalArgumentException} with the given message.
	 * 
	 * @param value
	 *            The value to be checked.
	 * @param message
	 *            The message of the exception.
	 */
	public static void notNull(Object value, String message) {
		if (value == null) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Validates if the given objects is not null. If it is not, throws an
	 * {@link IllegalArgumentException}.
	 * 
	 * @param value
	 *            The value to be checked.
	 */
	public static void notNull(Object value) {
		notNull(value, "Null value!");
	}

	/**
	 * Validates if the given String is not null and has a value grater than 0. If it is not, throws
	 * an {@link IllegalArgumentException} with the given message.
	 * 
	 * @param value
	 *            The value to be checked.
	 * @param message
	 *            The message of the exception.
	 */
	public static void notEmpty(String value, String message) {
		if (StringUtils.isEmpty(value)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Validates if the given String is not null and has a value grater than 0. If it is not, throws
	 * an {@link IllegalArgumentException}.
	 * 
	 * @param value
	 *            The value to be checked.
	 */
	public static void notEmpty(String value) {
		notEmpty(value, "Null or empty String!");
	}

	public static void inRange(int value, int start, int end, String message) {
		if (value < start && value > end) {
			Log.d("TTT", "Value : " + value + " | Start : " + start + " | End : " + end);
			throw new IllegalArgumentException(message);
		}
	}

	public static void inRange(int value, int start, int end) {
		inRange(value, start, end, "Not in range!");
	}

	public static void lowerThanOrEqual(int value, int end, String message) {
		if (value > end) {
			Log.d("TTT", "Value : " + value + " | Compared: " + end);
			throw new IllegalArgumentException(message);
		}
	}

	public static void lowerThanOrEqual(int value, int end) {
		lowerThanOrEqual(value, end, null);
	}

	public static void lowerThan(int value, int end, String message) {
		if (value >= end) {
			Log.d("TTT", "Value : " + value + " | Compared: " + end);
			throw new IllegalArgumentException(message);
		}
	}

	public static void lowerThan(int value, int end) {
		lowerThan(value, end, null);
	}

	public static void greaterThanOrEqual(int value, int start, String message) {
		if (value < start) {
			Log.d("TTT", "Value : " + value + " | Compared: " + start);
			throw new IllegalArgumentException(message);
		}
	}

	public static void greaterThanOrEqual(int value, int start) {
		greaterThanOrEqual(value, start, null);
	}

	public static void greaterThan(int value, int start, String message) {
		if (value <= start) {
			Log.d("TTT", "Value : " + value + " | Compared: " + start);
			throw new IllegalArgumentException(message);
		}
	}

	public static void greaterThan(int value, int start) {
		greaterThan(value, start, null);
	}
}
