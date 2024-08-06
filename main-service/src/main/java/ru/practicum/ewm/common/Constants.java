package ru.practicum.ewm.common;

import java.time.format.DateTimeFormatter;

public class Constants {

    public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTERN);

}
