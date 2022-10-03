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
        List<UserMealWithExcess> mealsFilteredByRecursion = filteredByRecursion(meals.size(), meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsFilteredByRecursion.forEach(System.out::println);
    }

    static Map<LocalDate, Integer> caloriesByDate = new HashMap<>();
    static List<UserMealWithExcess> resultList = new ArrayList<>();

    static List<UserMealWithExcess> filteredByRecursion(int i, List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int dayCaloriesLimit) {
        if (i != 0) {
            UserMeal userMeal = meals.get(--i);
            caloriesByDate.put(userMeal.getDate(), caloriesByDate.getOrDefault(userMeal.getDate(), 0) + userMeal.getCalories());
            filteredByRecursion(i, meals, startTime, endTime, dayCaloriesLimit);
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                resultList.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(),
                        caloriesByDate.get(userMeal.getDate()) > dayCaloriesLimit));
            }
        }
        return resultList;
    }
}