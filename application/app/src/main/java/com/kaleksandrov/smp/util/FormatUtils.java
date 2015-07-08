package com.kaleksandrov.smp.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by kaleksandrov on 5/8/15.
 */
public class FormatUtils {

    private static final String FORMATTER_PATTERN = "mm:ss";
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(FORMATTER_PATTERN,
            Locale.getDefault());

    public static String formatMinutesAndSeconds(int milliseconds) {
        return FORMATTER.format(milliseconds);
    }
}
