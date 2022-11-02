package ru.javawebinar.topjava.service;

import org.hibernate.LazyInitializationException;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    @Autowired
    private MealService service;

    private static final Logger logger = LoggerFactory.getLogger(MealServiceTest.class);
    private static final StringBuilder sbSummary = new StringBuilder();

    @AfterClass
    public static void onFinishingAllTests() {
        logger.info(sbSummary.toString());
    }

    @Rule
    public Stopwatch stopwatch = new Stopwatch() {
        @Override
        protected void finished(long nanos, Description description) {
            String infoString = description + " " + TimeUnit.NANOSECONDS.toMillis(nanos) + " ms";
            sbSummary.append('\n').append(infoString);
            logger.info(infoString);
        }
    };

    @Test
    public void delete() {
        service.delete(MEAL1_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(MEAL1_ID, USER_ID));
    }

    @Test
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER_ID));
    }

    @Test
    public void deleteNotOwn() {
        assertThrows(NotFoundException.class, () -> service.delete(MEAL1_ID, ADMIN_ID));
    }

    @Test
    public void create() {
        Meal created = service.create(getNew(), USER_ID);
        int newId = created.id();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        try {
            MEAL_MATCHER.assertMatch(created, newMeal);
        } catch (LazyInitializationException e) {
            if (created.getUser().getId() != USER_ID) {
                throw new NotFoundException("Wrong user Id in create() test");
            }
        }
        Meal actual = service.get(newId, USER_ID);
        try {
            MEAL_MATCHER.assertMatch(actual, newMeal);
        } catch (LazyInitializationException e) {
            if (actual.getUser().getId() != USER_ID) {
                throw new NotFoundException("Getting created meal failed in create() test");
            }
        }
    }

    @Test
    public void duplicateDateTimeCreate() {
        assertThrows(DataAccessException.class, () ->
                service.create(new Meal(null, meal1.getDateTime(), "duplicate", 100), USER_ID));
    }

    @Test
    public void get() {
        Meal actual = service.get(ADMIN_MEAL_ID, ADMIN_ID);
        try {
            MEAL_MATCHER.assertMatch(actual, adminMeal1);
        } catch (LazyInitializationException e) {
            if (actual.getUser().getId() != ADMIN_ID) {
                throw new NotFoundException("Getting meal of another user in get() test");
            }
        }
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void getNotOwn() {
        assertThrows(NotFoundException.class, () -> service.get(MEAL1_ID, ADMIN_ID));
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        Meal actual = service.get(MEAL1_ID, USER_ID);
        try {
            MEAL_MATCHER.assertMatch(actual, getUpdated());
        } catch (LazyInitializationException e) {
            if (actual.getUser().getId() != USER_ID) {
                throw new NotFoundException("Updating meal of another user in update() test");
            }
        }
    }

    @Test
    public void updateNotOwn() {
        assertThrows(NotFoundException.class, () -> service.update(meal1, ADMIN_ID));
        Meal actual = service.get(MEAL1_ID, USER_ID);
        try {
            MEAL_MATCHER.assertMatch(actual, meal1);
        } catch (LazyInitializationException e) {
            if (actual.getUser().getId() != USER_ID) {
                throw new NotFoundException("Wrong user Id in updateNotOwn() test");
            }
        }
    }

    @Test
    public void getAll() {
        List<Meal> allMeals = service.getAll(USER_ID);
        try {
            MEAL_MATCHER.assertMatch(allMeals, meals);
        } catch (LazyInitializationException e) {
            allMeals.forEach(meal -> {
                if (meal.getUser().getId() != USER_ID)
                    throw new NotFoundException("Wrong user Id in getAll() test");
            });
        }
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> allMealsBetween = service.getBetweenInclusive(
                LocalDate.of(2020, Month.JANUARY, 30),
                LocalDate.of(2020, Month.JANUARY, 30), USER_ID);
        try {
            MEAL_MATCHER.assertMatch(allMealsBetween, meal3, meal2, meal1);
        } catch (LazyInitializationException e) {
            allMealsBetween.forEach(meal -> {
                if (meal.getUser().getId() != USER_ID)
                    throw new NotFoundException("Wrong user Id in getAllBetweenInclusive() test");
            });
        }
    }

    @Test
    public void getBetweenWithNullDates() {
        List<Meal> allMealsBetween = service.getBetweenInclusive(null, null, USER_ID);
        try {
            MEAL_MATCHER.assertMatch(allMealsBetween, meals);
        } catch (LazyInitializationException e) {
            allMealsBetween.forEach(meal -> {
                if (meal.getUser().getId() != USER_ID)
                    throw new NotFoundException("Wrong user Id in getAllBetweenWithNullDates() test");
            });
        }
    }
}