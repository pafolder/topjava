package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.List;

public abstract class AbstractMealController {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private MealService service;

    public List<Meal> getAll() {
        log.info("getAllMeals for user {}", SecurityUtil.authUserId());
        return (List<Meal>) service.getAll(SecurityUtil.authUserId());
    }

    public Meal get(int id) {
        log.info("get Meal for user {}", SecurityUtil.authUserId());
        return service.get(id, SecurityUtil.authUserId());
    }

    public Meal update(Meal meal) {
        log.info("update Meal for user {}", SecurityUtil.authUserId());
        return service.update(meal, SecurityUtil.authUserId());
    }

    public void delete(int id) {
        log.info("delete Meal for user {}", SecurityUtil.authUserId());
        service.delete(id, SecurityUtil.authUserId());
    }

    public Meal create(Meal meal) {
        log.info("create Meal for user {}", SecurityUtil.authUserId());
        return service.create(meal, SecurityUtil.authUserId());
    }
}
