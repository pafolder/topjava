package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealsMemoryDao;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private final MealsMemoryDao meals = new MealsMemoryDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action != null && action.equalsIgnoreCase("delete")) {
            int mealId = Integer.parseInt(request.getParameter("mealId"));
            meals.delete(mealId);
        }
        if (action != null && action.equalsIgnoreCase("edit")) {
            int mealId = Integer.parseInt(request.getParameter("mealId"));
            request.setAttribute("meal", meals.get(mealId));
            request.getRequestDispatcher("edit.jsp").forward(request, response);
        }

        if (action != null && action.equalsIgnoreCase("new")) {
            LocalDateTime now = LocalDateTime.now();
            Meal meal = new Meal(now.minusNanos(now.getNano()).minusSeconds(now.getSecond()), "", 0, 0);
            int mealId = meals.add(meal);
            request.setAttribute("meal", meals.get(mealId));
            request.getRequestDispatcher("edit.jsp").forward(request, response);
        }
        processForwardRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String str = request.getParameter("dateTime").replaceFirst("T", " ");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        int mealId = Integer.parseInt(request.getParameter("mealId"));
        Meal meal = new Meal(dateTime,
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")),
                mealId);
        meals.update(meal);
        processForwardRequest(request, response);
    }

    private void processForwardRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("mealsTo",
                MealsUtil.filteredByStreams(meals.getAll(), LocalTime.of(0, 0), LocalTime.of(23, 59), 2000).stream()
                        .sorted((m1, m2) -> m1.getDateTime().isBefore(m2.getDateTime()) ? -1 : 0).collect(Collectors.toList()));
        request.getRequestDispatcher("meals.jsp").forward(request, response);
    }
}
