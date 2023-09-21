package com.datdeveloper.datfactions.util;

public class AgeUtil {
    private AgeUtil() {
        throw new IllegalStateException("Utility class");
    }
    /**
     * Get a string explaining how long ago the timestamp was<br>
     * For example, if the timestamp represents 3500 seconds ago, it would return "58 minutes"
     * Supports seconds, minutes, hours, days, months, and years
     * @param startingTime The timestamp
     * @return a string representing the time since the timestamp
     */
    public static String calculateAgeString(final long startingTime) {
        final long delta = (System.currentTimeMillis() - startingTime) / 1000;

        if (delta < 60){
            return delta + " Second(s)";
        } else if (delta < 3_600) {
            return (delta / 60) + " Minute(s)";
        } else if (delta < 86_400) {
            return (delta / 3_600) + " Hour(s)";
        } else if (delta < 2_592_000) {
            return (delta / 86_400) + " Day(s)";
        } else if (delta < 31_104_000) {
            return (delta / 2_592_000) + " Month(s)";
        } else {
            return (delta / 31_104_000) + " Year(s)";
        }
    }
}
