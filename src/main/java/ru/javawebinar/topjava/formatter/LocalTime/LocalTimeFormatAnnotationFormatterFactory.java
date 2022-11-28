package ru.javawebinar.topjava.formatter.LocalTime;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocalTimeFormatAnnotationFormatterFactory implements AnnotationFormatterFactory<LocalTimeFormat> {
    @Override
    public Set<Class<?>> getFieldTypes() {
        return new HashSet<>(List.of(LocalTime.class));
    }

    @Override
    public Printer<LocalTime> getPrinter( LocalTimeFormat annotation, Class<?> fieldType) {

        return new LocalTimeFormatter();
    }

    @Override
    public Parser<LocalTime> getParser(LocalTimeFormat annotation, Class<?> fieldType) {
        return new LocalTimeFormatter();
    }
}
