package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.meal.MealRestController;
import ru.javawebinar.topjava.web.user.AdminRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MealServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    MealRestController mealRestController;
    FormDateTimeFilter formDateTimeFilter;

    @Override
    public void init() {
        // java 7 automatic resource management (ARM)
        try (ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml")) {
            System.out.println("Bean definition names: " + Arrays.toString(appCtx.getBeanDefinitionNames()));
            AdminRestController adminUserController = appCtx.getBean(AdminRestController.class);
            adminUserController.create(new User(null, "userName", "email@mail.ru", "password", Role.ADMIN));

            mealRestController = appCtx.getBean(MealRestController.class);
            List<Meal> meals = mealRestController.getAll();
            log.debug("meals: {}", meals);
            formDateTimeFilter = new FormDateTimeFilter(mealRestController.getAll());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")), SecurityUtil.authUserId());

        log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
        mealRestController.update(meal);
        formDateTimeFilter.initFilter(mealRestController.getAll());
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                mealRestController.delete(id);
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000, SecurityUtil.authUserId()) :
                        mealRestController.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "selectUser":
                SecurityUtil.setUserId(Integer.parseInt(request.getParameter("user")));
                formDateTimeFilter.initFilter(mealRestController.getAll());
                response.sendRedirect("meals");
                break;
            case "filter":
                formDateTimeFilter.readParameters(request);
            case "all":
            default:
                request.setAttribute("userId", SecurityUtil.authUserId());
                request.setAttribute("formDateTimeFilter", formDateTimeFilter);
                request.setAttribute("meals",
                        MealsUtil.getFilteredTos(mealRestController.getAll(), MealsUtil.DEFAULT_CALORIES_PER_DAY,
                                        formDateTimeFilter.startTime,
                                        formDateTimeFilter.endTime.plus(ChronoUnit.SECONDS.getDuration())).stream()
                                .filter(mealTo -> mealTo.getDateTime().toLocalDate().isAfter(formDateTimeFilter.startDate.minus(Period.ofDays(1)))
                                        && mealTo.getDateTime().toLocalDate().isBefore(formDateTimeFilter.endDate.plus(Period.ofDays(1))))
                                .collect(Collectors.toList()));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

    public class FormDateTimeFilter {
        private LocalDate startDate = LocalDate.now();
        public LocalDate endDate = LocalDate.now();
        public LocalTime startTime = LocalTime.now();
        public LocalTime endTime = LocalTime.now();

        public FormDateTimeFilter(List<Meal> meals) {
            initFilter(meals);
        }

        public void initFilter(List<Meal> meals) {
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
                formDateTimeFilter.startDate = LocalDate.parse(string);
            }
            if ((string = request.getParameter("filterEndDate")) != null) {
                formDateTimeFilter.endDate = LocalDate.parse(string);
            }
            if ((string = request.getParameter("filterStartTime")) != null) {
                formDateTimeFilter.startTime = LocalTime.parse(string);
            }
            if ((string = request.getParameter("filterEndTime")) != null) {
                formDateTimeFilter.endTime = LocalTime.parse(string);
            }
        }
    }
}
