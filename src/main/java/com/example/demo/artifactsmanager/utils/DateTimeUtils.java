package com.example.demo.artifactsmanager.utils;

import com.example.demo.artifactsmanager.enums.DateTimePattern;
import lombok.NonNull;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public interface DateTimeUtils {

    static ZonedDateTime getCurrentZonedDateTime() {
        return ZonedDateTime.now();
    }

    static DateTimeFormatter getFormatter(@NonNull String pattern) {
        return DateTimeFormatter.ofPattern(pattern);
    }

    static String getDaysMinus(@NonNull DateTimePattern pattern, int noOfDaysToMinus) {
        return ZonedDateTime.now().minusDays(noOfDaysToMinus).format(getFormatter(pattern.getPattern()));
    }
}
