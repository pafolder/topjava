package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class MealRestController {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private MealService service;

    public List<Meal> getAll() {
        log.info("getAllMeals for user {}", SecurityUtil.authUserId());
        return (List<Meal>) service.getAll(SecurityUtil.authUserId());
    }

    public List<MealTo> getAllTosFilteredByDate(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        return MealsUtil.getFilteredTos(service.getAllFilteredByDate(startDate, endDate, SecurityUtil.authUserId()),
                MealsUtil.DEFAULT_CALORIES_PER_DAY, startTime, endTime);
    }

    public Meal get(int id) {
        log.info("get Meal for user {}", SecurityUtil.authUserId());
        return service.get(id, SecurityUtil.authUserId());
    }

    public void delete(int id) {
        log.info("delete Meal for user {}", SecurityUtil.authUserId());
        service.delete(id, SecurityUtil.authUserId());
    }

    public Meal create(Meal meal) {
        log.info("create Meal for user {}", SecurityUtil.authUserId());
        checkNew(meal);
        return service.create(meal, SecurityUtil.authUserId());
    }

    public Meal update(Meal meal, int id) {
        assureIdConsistent(meal, id);
        log.info("update Meal for user {}", SecurityUtil.authUserId());
        return service.update(meal, SecurityUtil.authUserId());
    }
}
