package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class MealServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    private ConfigurableApplicationContext context;
    private MealRestController mealRestController;

    @Override
    public void init() {
        // java 7 automatic resource management (ARM)
        try {
            context = new ClassPathXmlApplicationContext("spring/spring-app.xml");
            mealRestController = context.getBean(MealRestController.class);
        } catch (BeansException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        Meal meal = action.equals("delete") ? null : new Meal(
                request.getParameter("id").isEmpty() ? null : Integer.valueOf(request.getParameter("id")),
                LocalDateTime.parse(request.getParameter("dateTime")), request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")), null);

        switch (action) {
            case "delete":
                mealRestController.delete(getId(request));
                break;
            case "update":
                mealRestController.update(meal, getId(request));
                break;
            case "create":
                mealRestController.create(meal);
                break;
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        String action = request.getParameter("action");
        switch (action == null ? "all" : action) {
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000, 0) :
                        mealRestController.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
            default:
                String string;
                string = request.getParameter("fStartDate");
                LocalDate fStartDate = StringUtils.hasLength(string) ? LocalDate.parse(string) : null;
                string = request.getParameter("fEndDate");
                LocalDate fEndDate = StringUtils.hasLength(string) ? LocalDate.parse(string) : null;
                string = request.getParameter("fStartTime");
                LocalTime fStartTime = StringUtils.hasLength(string) ? LocalTime.parse(string) : null;
                string = request.getParameter("fEndTime");
                LocalTime fEndTime = StringUtils.hasLength(string) ? LocalTime.parse(string) : null;
                request.setAttribute("meals", mealRestController.getAllTosFiltered(
                        fStartDate, fEndDate, fStartTime, fEndTime));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

    @Override
    public void destroy() {
        context.close();
        super.destroy();
    }
}
