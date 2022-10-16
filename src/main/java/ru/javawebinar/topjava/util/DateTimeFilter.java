package ru.javawebinar.topjava.util;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;

public class DateTimeFilter {
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;

    public DateTimeFilter(HttpServletRequest request) {
        String string;
        if ((string = request.getParameter("filterStartDate")) != null) {
            startDate = string.length() > 0 ? LocalDate.parse(string) : null;
        }
        if ((string = request.getParameter("filterEndDate")) != null) {
            endDate = string.length() > 0 ? LocalDate.parse(string) : null;
        }
        if ((string = request.getParameter("filterStartTime")) != null) {
            startTime = string.length() > 0 ? LocalTime.parse(string) : null;
        }
        if ((string = request.getParameter("filterEndTime")) != null) {
            endTime = string.length() > 0 ? LocalTime.parse(string) : null;
        }
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
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

}