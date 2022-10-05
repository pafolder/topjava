package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.*;
import java.util.*;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
        System.out.println("Filtered by recursion (Bonus):");
        List<UserMealWithExcess> mealsFilteredByRecursion = filteredByRecursion(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsFilteredByRecursion.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByRecursion(List<UserMeal> _meals, LocalTime _startTime, LocalTime _endTime, int _dayCaloriesLimit) {
        return new Runnable() {
            final List<UserMeal> meals = _meals;
            final LocalTime startTime = _startTime;
            final LocalTime endTime = _endTime;
            final int dayCaloriesLimit = _dayCaloriesLimit;
            final Map<LocalDate, Integer> caloriesByDate = new HashMap<>();
            final List<UserMealWithExcess> resultList = new ArrayList<>();
            int i = meals.size();

            @Override
            public void run() {
                if (i != 0) {
                    UserMeal userMeal = meals.get(--i);
                    caloriesByDate.put(userMeal.getDate(), caloriesByDate.getOrDefault(userMeal.getDate(), 0) + userMeal.getCalories());
                    run();
                    if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                        resultList.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(),
                                caloriesByDate.get(userMeal.getDate()) > dayCaloriesLimit));
                    }
                }
            }

            List<UserMealWithExcess> doFiltering() {
                run();
                return resultList;
            }
        }.doFiltering();
    }
}
