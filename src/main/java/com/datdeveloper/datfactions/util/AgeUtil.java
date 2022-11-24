package com.datdeveloper.datfactions.util;

public class AgeUtil {
    public static String calculateAgeString(final long startingTime) {
        final long delta = (System.currentTimeMillis() - startingTime) / 1000;

        if (delta < 60){
            return delta + " Seconds";
        } else if (delta < 3600) {
            return (delta / 60) + " Minutes";
        } else if (delta < 86400) {
            return (delta / 3600) + " Hours";
        } else if (delta < 2592000) {
            return (delta / 86400) + " Months";
        } else {
            return (delta / 2592000) + " Years";
        }
    }
}
