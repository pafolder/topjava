package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.web.RootController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
public class JspMealRestController extends AbstractMealController {
    private static final Logger log = LoggerFactory.getLogger(RootController.class);

    @Autowired
    public JspMealRestController(MealService service) {
        super(service);
    }

    @GetMapping("/meals")
    public String getMeals(HttpServletRequest request) {
        log.info("meals");
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "create" -> {
                request.setAttribute("meal",
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000));
                return "mealForm";
            }
            case "filter" -> {
                LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
                LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
                LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
                LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
                request.setAttribute("meals", getBetween(startDate, startTime, endDate, endTime));
                return "meals";
            }
            default -> {
                request.setAttribute("meals", getAll());
                return "meals";
            }
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

    @PostMapping("/meals")
    public String postMeals(HttpServletRequest request) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");

        if ("Update".equals(request.getParameter("update"))) {
            request.setAttribute("meal", get(getId(request)));
            return "mealForm";
        }

        if ("Delete".equals(request.getParameter("delete"))) {
            delete(getId(request));
            return "redirect:meals";
        }

        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        if (StringUtils.hasLength(request.getParameter("id"))) {
            update(meal, getId(request));
        } else {
            create(meal);
        }
        return "redirect:meals";
    }
}
