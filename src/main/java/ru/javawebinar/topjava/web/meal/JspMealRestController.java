package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;

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
    @Autowired
    public JspMealRestController(MealService service) {
        super(service);
    }

    @GetMapping("meals")
    public String getAll(HttpServletRequest request) {
        log.info("meals");
        request.setAttribute("meals", getAll());
        return "meals";
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

    @PostMapping("meals/save")
    public String postSave(HttpServletRequest request) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        if (StringUtils.hasLength(request.getParameter("id"))) {
            update(meal, getId(request));
        } else {
            create(meal);
        }
        return "redirect:/meals";
    }

    @GetMapping("meals/update")
    public String postUpdate(HttpServletRequest request) {
        request.setAttribute("meal", get(getId(request)));
        return "mealForm";
    }

    @PostMapping("meals/delete")
    public String postDelete(HttpServletRequest request) {
        if (request.getParameter("delete") != null) {
            int id = getId(request);
            delete(id);
        }
        return "redirect:/meals";
    }

    @PostMapping("meals/filter")
    public String postFilter(HttpServletRequest request) {
        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
        request.setAttribute("meals", getBetween(startDate, startTime, endDate, endTime));
        log.info("Filters forward");
        return "meals";
    }

    @PostMapping("meals/add")
    public String postAdd(HttpServletRequest request) {
        request.setAttribute("meal",
                new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000));
        return "mealForm";
    }
}