package ru.javawebinar.topjava.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

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
    MealService service;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void get() {
        Meal newMeal = new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 9, 1), "Завтрак", 500);
        Meal createdMeal = service.create(newMeal, USER_ID);
        assertThat(service.get(createdMeal.getId(), USER_ID)).usingRecursiveComparison().isEqualTo(createdMeal);
    }

    @Test
    public void delete() {
        Meal newMeal = new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 1), "Завтрак", 500);
        Meal createdMeal = service.create(newMeal, USER_ID);
        Integer newMealId = createdMeal.getId();
        service.delete(newMealId, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(newMealId, USER_ID));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> meals = service.getBetweenInclusive(
                LocalDate.of(2020, Month.JANUARY, 30),
                LocalDate.of(2020, Month.JANUARY, 30),
                USER_ID);
        assertEquals(4, meals.size());
    }

    @Test
    public void getAll() {
        final int[] initialId = {100003};
        List<Meal> allMeals = service.getAll(USER_ID);
        List<Meal> testMeals = MealsUtil.meals;
        testMeals.forEach(meal -> meal.setId(initialId[0]++));
        assertThat(allMeals).usingRecursiveComparison().isEqualTo(testMeals);
    }

    @Test
    public void update() {
        Meal mealToUptate = service.create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 1), "Завтрак", 500), USER_ID);
        mealToUptate.setCalories(mealToUptate.getCalories() + 1);
        mealToUptate.setDescription(mealToUptate.getDescription() + " updated");
        mealToUptate.setDateTime(mealToUptate.getDateTime().plusDays(1));
        service.update(mealToUptate, USER_ID);
        Meal updatedMeal = service.get(mealToUptate.getId(), USER_ID);
        assertThat(updatedMeal).usingRecursiveComparison().isEqualTo(mealToUptate);
    }

    @Test
    public void getDeleteUpdadeNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(0, USER_ID));
        assertThrows(NotFoundException.class, () -> service.delete(0, USER_ID));
        assertThrows(NotFoundException.class, () -> service.update(service.getAll(USER_ID).get(0), USER_ID + 1));
    }

    @Test
    public void create() {
        Meal newMeal = new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 1), "Завтрак", 500);
        Meal createdMeal = service.create(newMeal, USER_ID);
        Integer newId = createdMeal.getId();
        newMeal.setId(newId);
        assertThat(newMeal).usingRecursiveComparison().isEqualTo(createdMeal);
    }

    @Test
    public void createForSameDateTime() {
        Meal newMeal = new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 1), "Завтрак", 500);
        Meal newMealWithSameDateTime = new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 1), "Второй завтрак", 555);
        service.create(newMeal, USER_ID);
        assertThrows(DuplicateKeyException.class, () -> service.create(newMealWithSameDateTime, USER_ID));
    }
}