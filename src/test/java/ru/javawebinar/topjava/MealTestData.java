package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MealTestData {
    public static final int MEAL_START_ID = 100003;
    public static final int NOT_FOUND = 10;
    public static final LocalDate START_DATE = LocalDate.of(2020, Month.JANUARY, 30);
    public static final LocalDate END_DATE = LocalDate.of(2020, Month.JANUARY, 30);

    public static List<Meal> meals = Arrays.asList(
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
    );

    public static List<Meal> oneDayMeals = Arrays.asList(meals.get(2), meals.get(1), meals.get(0));

    static {
        final int[] i = {MEAL_START_ID};
        meals.forEach(meal -> meal.setId(i[0]++));
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static Meal getNew() {
        return new Meal(LocalDateTime.of(2021, Month.MAY, 1, 5, 0), "Ранний завтрак", 50);
    }

    public static Meal getUpdated() {
        Meal updatedMeal = getNew();
        updatedMeal.setId(meals.get(0).getId());
        return updatedMeal;
    }
}
