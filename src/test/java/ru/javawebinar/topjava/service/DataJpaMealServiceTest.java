package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.model.UserWithMeals;
import ru.javawebinar.topjava.service.basic.AbstractMealServiceTest;

import static ru.javawebinar.topjava.MealTestData.MEAL_MATCHER;
import static ru.javawebinar.topjava.MealTestData.meals;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ActiveProfiles({"datajpa", "hsqldb"})
public class DataJpaMealServiceTest extends AbstractMealServiceTest {
    UserWithMeals userWithMeal;

    @Test
    public void userWithMealTest() {
        userWithMeal = service.getUserWithMeals(USER_ID);
        MEAL_MATCHER.assertMatch(userWithMeal.getMeals(), meals);

        if (userWithMeal != null) {
            log.info("UserWithMeal " + userWithMeal.getUser().getName() + " has " + userWithMeal.getMeals().size() + " meals");
        }
    }
}

