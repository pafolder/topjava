package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int MEAL_START_ID = START_SEQ + 3;
    public static final int MEAL_NOT_FOUND = START_SEQ - 1;
    public static final LocalDate START_DATE = LocalDate.of(2020, Month.JANUARY, 30);
    public static final LocalDate END_DATE = LocalDate.of(2020, Month.JANUARY, 30);

    public static final Meal meal_1 = new Meal(MEAL_START_ID,
            LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500);
    public static final Meal meal_2 = new Meal(MEAL_START_ID + 1,
            LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000);
    public static final Meal meal_3 = new Meal(MEAL_START_ID + 2,
            LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500);
    public static final Meal meal_4 = new Meal(MEAL_START_ID + 3,
            LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100);
    public static final Meal meal_5 = new Meal(MEAL_START_ID + 4,
            LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000);
    public static final Meal meal_6 = new Meal(MEAL_START_ID + 5,
            LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500);
    public static final Meal meal_7 = new Meal(MEAL_START_ID + 6,
            LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410);

    public static final List<Meal> oneDayMeals = Arrays.asList(
            meal_3,
            meal_2,
            meal_1
    );

    public static final List<Meal> mealsSortedNewToOld = Arrays.asList(
            meal_7,
            meal_6,
            meal_5,
            meal_4,
            meal_3,
            meal_2,
            meal_1
    );

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static Meal getUpdated() {
        Meal updatedMeal = getNew();
        updatedMeal.setId(MEAL_START_ID);
        return updatedMeal;
    }

    public static Meal getNew() {
        return new Meal(null,
                LocalDateTime.of(2021, Month.MAY, 1, 5, 0), "Новая еда", 50);
    }
}