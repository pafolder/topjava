package ru.javawebinar.topjava.formatter.LocalDate;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocalDateFormatAnnotationFormatterFactory implements AnnotationFormatterFactory<LocalDateFormat> {
    @Override
    public Set<Class<?>> getFieldTypes() {
        return new HashSet<>(List.of(LocalDate.class));
    }

    @Override
    public Printer<LocalDate> getPrinter(LocalDateFormat annotation, Class<?> fieldType) {
        return new LocalDateFormatter();
    }

    @Override
    public Parser<LocalDate> getParser(LocalDateFormat annotation, Class<?> fieldType) {
        return new LocalDateFormatter();
    }
}
