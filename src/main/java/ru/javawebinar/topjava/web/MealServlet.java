package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealsDao;
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
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final int CALORIES_PER_DAY = 2000;
    private static final Logger log = getLogger(MealServlet.class);
    private final MealsDao meals = new MealsMemoryDao();
    private int temporaryMealId;

    @Override
    public void init() throws ServletException {
        super.init();
        meals.addTestData();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action != null) {
            switch (action) {
                case "delete":
                    int mealId = Integer.parseInt(request.getParameter("mealId"));
                    meals.delete(mealId);
                    break;
                case "edit":
                    log.debug("mealId = {}", request.getParameter("mealId"));
                    mealId = Integer.parseInt(request.getParameter("mealId"));
                    request.setAttribute("meal", meals.get(mealId));
                    request.setAttribute("actionInCaption", "Edit");
                    request.getRequestDispatcher("editmeal.jsp").forward(request, response);
                    return;
                case "new":
                    Meal temporaryMeal = new Meal(0, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 0);
                    temporaryMealId = meals.add(temporaryMeal).getId();
                    request.setAttribute("meal", meals.get(temporaryMealId));
                    request.setAttribute("actionInCaption", "New");
                    request.getRequestDispatcher("editmeal.jsp").forward(request, response);
                    return;
                case "newCanceled":
                    meals.delete(temporaryMealId);
                    break;
            }
        }
        createMealsToSetAttributeForward(request, response);
    }

    private void createMealsToSetAttributeForward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("mealsTo", MealsUtil.filteredByStreams(meals.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY).stream()
                .sorted((m1, m2) -> m1.getDateTime().isBefore(m2.getDateTime()) ? -1 : 0).collect(Collectors.toList()));
        request.getRequestDispatcher("meals.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        int mealId = Integer.parseInt(request.getParameter("mealId"));
        Meal meal = new Meal(mealId,
                LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(request.getParameter("dateTime"))),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories"))
        );
        meals.update(meal);
        createMealsToSetAttributeForward(request, response);
    }
}
