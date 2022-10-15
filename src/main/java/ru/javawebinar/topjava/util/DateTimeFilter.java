package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class DateTimeFilter {
    private LocalDate startDate = LocalDate.now();
    private LocalDate endDate = LocalDate.now();
    private LocalTime startTime = LocalTime.now();
    private LocalTime endTime = LocalTime.now();

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public DateTimeFilter(List<Meal> meals) {
        resetFilter(meals);
    }

    public void resetFilter(List<Meal> meals) {
        if (meals.size() != 0) {
            startDate = meals.get(0).getDate();
            endDate = meals.get(0).getDate();
            startTime = meals.get(0).getTime();
            endTime = meals.get(0).getTime();

            meals.forEach(meal -> {
                        if (startDate.isAfter(meal.getDate())) {
                            startDate = meal.getDate();
                        }
                        if (endDate.isBefore(meal.getDate())) {
                            endDate = meal.getDate();
                        }
                        if (startTime.isAfter(meal.getTime())) {
                            startTime = meal.getTime();
                        }
                        if (endTime.isBefore(meal.getTime())) {
                            endTime = meal.getTime();
                        }
                    }
            );
        }
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void readParameters(HttpServletRequest request) {
        String string;
        if ((string = request.getParameter("filterStartDate")) != null) {
            startDate = LocalDate.parse(string);
        }
        if ((string = request.getParameter("filterEndDate")) != null) {
            endDate = LocalDate.parse(string);
        }
        if ((string = request.getParameter("filterStartTime")) != null) {
            startTime = LocalTime.parse(string);
        }
        if ((string = request.getParameter("filterEndTime")) != null) {
            endTime = LocalTime.parse(string);
        }
    }
}