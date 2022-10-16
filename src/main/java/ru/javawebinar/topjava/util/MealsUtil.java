package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MealsUtil {
    public static final int DEFAULT_CALORIES_PER_DAY = 2000;

    public static final List<Meal> mealsForUser1 = Arrays.asList(
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500, SecurityUtil.authUserId()),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000, SecurityUtil.authUserId()),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500, SecurityUtil.authUserId()),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100, SecurityUtil.authUserId()),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000, SecurityUtil.authUserId()),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500, SecurityUtil.authUserId()),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410, SecurityUtil.authUserId())
    );

    public static final List<Meal> mealsForUser2 = Arrays.asList(
            new Meal(LocalDateTime.of(2022, Month.JANUARY, 30, 10, 0), "Завтрак", 500, SecurityUtil.authUserId()),
            new Meal(LocalDateTime.of(2022, Month.JANUARY, 30, 13, 0), "Обед", 1000, SecurityUtil.authUserId()),
            new Meal(LocalDateTime.of(2022, Month.JANUARY, 30, 20, 0), "Ужин", 500, SecurityUtil.authUserId()),
            new Meal(LocalDateTime.of(2022, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100, SecurityUtil.authUserId()),
            new Meal(LocalDateTime.of(2022, Month.JANUARY, 31, 10, 0), "Завтрак", 1000, SecurityUtil.authUserId()),
            new Meal(LocalDateTime.of(2022, Month.JANUARY, 31, 13, 0), "Обед", 500, SecurityUtil.authUserId()),
            new Meal(LocalDateTime.of(2022, Month.JANUARY, 31, 23, 0), "Наесться впрок", 9999, SecurityUtil.authUserId()),
            new Meal(LocalDateTime.of(2021, Month.DECEMBER, 31, 0, 0), "Еда на переедание", 2001, SecurityUtil.authUserId()),
            new Meal(LocalDateTime.of(2021, Month.DECEMBER, 30, 11, 30), "Второй завтрак", 1000, SecurityUtil.authUserId()),
            new Meal(LocalDateTime.of(2021, Month.DECEMBER, 29, 7, 15), "Завтрак на скорость", 500, SecurityUtil.authUserId())
    );

    public static List<MealTo> getFilteredTos(Collection<Meal> meals, int caloriesPerDay, LocalTime startTime, LocalTime endTime) {
        return filterByPredicate(meals, caloriesPerDay, meal -> DateTimeUtil.isBetween(meal.getTime(), startTime, endTime));
    }

    private static List<MealTo> filterByPredicate(Collection<Meal> meals, int caloriesPerDay, Predicate<Meal> filter) {
        Map<LocalDate, Integer> caloriesSumByDate = meals.stream()
                .collect(
                        Collectors.groupingBy(Meal::getDate, Collectors.summingInt(Meal::getCalories))
//                      Collectors.toMap(Meal::getDate, Meal::getCalories, Integer::sum)
                );

        return meals.stream()
                .filter(filter)
                .map(meal -> createTo(meal, caloriesSumByDate.get(meal.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    private static MealTo createTo(Meal meal, boolean excess) {
        return new MealTo(meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess);
    }
}
