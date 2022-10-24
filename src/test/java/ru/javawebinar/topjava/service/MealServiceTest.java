package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.MealTestData.NOT_FOUND;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.GUEST_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(MEAL_START_ID, USER_ID);
        assertMatch(meal, meals.get(0));
    }

    @Test
    public void getMealNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void getUserNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, NOT_FOUND));
    }

    @Test
    public void delete() {
        service.delete(MEAL_START_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(MEAL_START_ID, USER_ID));
    }

    @Test
    public void deleteMealNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER_ID));
    }

    @Test
    public void deleteUserNotFound() {
        service.delete(MEAL_START_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(MEAL_START_ID, NOT_FOUND));
    }

    @Test
    public void getAll() {
        List<Meal> all = service.getAll(USER_ID);
        MealTestData.assertMatch(new HashSet<>(all), new HashSet<>(meals));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> filteredMeals = service.getBetweenInclusive(START_DATE, END_DATE, USER_ID);
        assertMatch(filteredMeals, oneDayMeals);
    }

    @Test
    public void getBetweenInclusiveNullNull() {
        List<Meal> filteredMeals = service.getBetweenInclusive(null, null, USER_ID);
        assertEquals(meals.size(), filteredMeals.size());
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.get(meals.get(0).getId(), USER_ID), getUpdated());
    }

    @Test
    public void updateUserNotFound() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdated(), NOT_FOUND));
    }

    @Test
    public void updateMealsOfOtherUsers() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdated(), ADMIN_ID));
        assertThrows(NotFoundException.class, () -> service.update(getUpdated(), GUEST_ID));
    }

    @Test
    public void create() {
        Meal created = service.create(getNew(), USER_ID);
        Integer newId = created.getId();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void createForSameDateTime() {
        service.create(getNew(), USER_ID);
        assertThrows(DuplicateKeyException.class, () -> service.create(getNew(), USER_ID));
    }

    @Test
    public void createUserNotFound() {
        assertThrows(NotFoundException.class, () -> service.create(getNew(), NOT_FOUND));
    }
}
