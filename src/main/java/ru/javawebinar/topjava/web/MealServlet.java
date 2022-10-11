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
import java.time.Month;
import java.time.temporal.ChronoUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final int CALORIES_PER_DAY = 2000;
    private static final Logger log = getLogger(MealServlet.class);
    private final MealsDao meals = new MealsMemoryDao();

    @Override
    public void init() {
        meals.add(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        meals.add(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        meals.add(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        meals.add(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        meals.add(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        meals.add(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        meals.add(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action != null) {
            switch (action) {
                case "delete":
                    int mealId = Integer.parseInt(request.getParameter("mealId"));
                    meals.delete(mealId);
                    response.sendRedirect("meals");
                    return;
                case "edit":
                    log.debug("mealId = {}", request.getParameter("mealId"));
                    mealId = Integer.parseInt(request.getParameter("mealId"));
                    request.setAttribute("meal", meals.get(mealId));
                    request.setAttribute("isUpdate", true);
                    request.getRequestDispatcher("editmeal.jsp").forward(request, response);
                    return;
                case "new":
                    Meal temporaryMeal = new Meal(null, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 0);
                    request.setAttribute("meal", temporaryMeal);
                    request.setAttribute("isUpdate", false);
                    request.getRequestDispatcher("editmeal.jsp").forward(request, response);
                    return;
            }
        }
        createMealsToSetAttributeForward(request, response);
    }

    private void createMealsToSetAttributeForward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("mealsTo", MealsUtil.filteredByStreams(meals.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY));
        request.getRequestDispatcher("meals.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String mealIdString = request.getParameter("mealId");
        Integer mealId = (mealIdString.length() != 0) ? Integer.parseInt(mealIdString) : null;
        Meal meal = new Meal(mealId,
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories"))
        );
        meals.update(meal);
        createMealsToSetAttributeForward(request, response);
    }
}
