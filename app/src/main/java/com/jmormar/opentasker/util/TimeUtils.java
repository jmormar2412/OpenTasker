package com.jmormar.opentasker.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TimeUtils {

    public static LocalTime getNextHour(LocalTime dateTime) {
        return dateTime.plusHours(1).truncatedTo(ChronoUnit.HOURS);
    }

    public static long minutesUntilNextHour(LocalTime dateTime) {
        LocalTime nextHour = getNextHour(dateTime);
        if (dateTime.isAfter(nextHour) || dateTime.equals(nextHour)) {
            nextHour = nextHour.plusHours(24);
        }
        return ChronoUnit.MINUTES.between(dateTime, nextHour);
    }

    public static String getBaseHour(LocalTime dateTime) {
        LocalTime baseHour = dateTime.truncatedTo(ChronoUnit.HOURS);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return baseHour.format(formatter);
    }
}

