package ru.javawebinar.topjava.formatter.LocalTime;

import org.springframework.format.Formatter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalTimeFormatter implements Formatter<LocalTime> {

    @Override
    public LocalTime parse(String text, Locale locale) {
        if (text.length() == 0 || text.contains("null")) {
            return LocalTime.MIN;
        }
        return LocalTime.parse(text);
    }

    @Override
    public String print(LocalTime time, Locale locale) {
        return time.format(DateTimeFormatter.ISO_LOCAL_TIME);
    }
}
