package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.base.AbstractMealServiceTest;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_MATCHER;

@ActiveProfiles(Profiles.DATAJPA)
public class DataJpaMealServiceTest extends AbstractMealServiceTest {
    @Test
    @Transactional
    public void getMealWithUserTest() {
        Meal mealWithUser = service.getMealWithUser(MEAL1_ID);
        MEAL_MATCHER.assertMatch(mealWithUser, meal1);
        USER_MATCHER.assertMatch(mealWithUser.getUser(), UserTestData.user);
    }

    @Test
    public void getMealWithUserTestNotFound() {
        assertThrows(NotFoundException.class, () -> service.getMealWithUser(NOT_FOUND));
    }
}

