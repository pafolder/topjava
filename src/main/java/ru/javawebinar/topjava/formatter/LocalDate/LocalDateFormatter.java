package ru.javawebinar.topjava.formatter.LocalDate;

import org.springframework.format.Formatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateFormatter implements Formatter<LocalDate> {
    @Override
    public String print(LocalDate date, Locale locale) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public LocalDate parse(String text, Locale locale) {
        if (text.length() == 0 || text.contains("null")) {
            return LocalDate.MIN;
        }
        return LocalDate.parse(text);
    }
}
