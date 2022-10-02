package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

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

        System.out.println("Filtered by Cycles Optional 2:");
        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println("\nFiltered by Streams Optional 2:");
        List<UserMealWithExcess> mealsToOptional = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsToOptional.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, FilteredMealsPerDayWithTotalCarlories> fMealsMap = new HashMap<>();
        meals.forEach(userMeal -> {
            FilteredMealsPerDayWithTotalCarlories fMeals = new FilteredMealsPerDayWithTotalCarlories(caloriesPerDay);
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                fMeals.addMeal(userMeal);
            else
                fMeals.addOnlyCalories(userMeal);
            if (fMealsMap.containsKey(userMeal.getDate())) {
                FilteredMealsPerDayWithTotalCarlories existingMeals = fMealsMap.get(userMeal.getDate());
                existingMeals.totalCaloriesPerDay += fMeals.totalCaloriesPerDay;
                if (fMeals.filteredMealsPerDay.size() != 0)
                    existingMeals.filteredMealsPerDay.add(fMeals.filteredMealsPerDay.get(0));
                existingMeals.processExcess();
                fMealsMap.put(userMeal.getDate(), existingMeals);
                fMeals = existingMeals;
            }
            fMealsMap.put(userMeal.getDate(), fMeals);
        });
        List<UserMealWithExcess> listWithExcess = new ArrayList<>();
        fMealsMap.values().forEach(fMeals -> listWithExcess.addAll(fMeals.filteredMealsPerDay));

        return listWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .collect(Collectors.toMap(UserMeal::getDate,
                        userMeal -> {
                            FilteredMealsPerDayWithTotalCarlories fMeals = new FilteredMealsPerDayWithTotalCarlories(caloriesPerDay);
                            return (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) ?
                                    fMeals.addMeal(userMeal) : fMeals.addOnlyCalories(userMeal);
                        },
                        (existing, newMeal) -> {
                            existing.totalCaloriesPerDay += newMeal.totalCaloriesPerDay;
                            if (newMeal.filteredMealsPerDay.size() != 0)
                                existing.filteredMealsPerDay.add(newMeal.filteredMealsPerDay.get(0));
                            existing.processExcess();
                            return existing;
                        }
                )).values().stream()
                .map(fMealsPerDay -> fMealsPerDay.filteredMealsPerDay)
                .collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList());
    }

    static class FilteredMealsPerDayWithTotalCarlories {
        public List<UserMealWithExcess> filteredMealsPerDay = new ArrayList<>();
        public int totalCaloriesPerDay = 0;
        private final int limitCaloriesPerDay;
        private boolean dayExcess = false;

        public FilteredMealsPerDayWithTotalCarlories(int limitCaloriesPerDay) {
            this.limitCaloriesPerDay = limitCaloriesPerDay;
        }

        private FilteredMealsPerDayWithTotalCarlories addMeal(UserMeal userMeal) {
            filteredMealsPerDay.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), dayExcess));
            addOnlyCalories(userMeal);
            processExcess();
            return this;
        }

        private FilteredMealsPerDayWithTotalCarlories addOnlyCalories(UserMeal userMeal) {
            totalCaloriesPerDay += userMeal.getCalories();
            processExcess();
            return this;
        }

        public void processExcess() {
            if (!dayExcess && totalCaloriesPerDay > limitCaloriesPerDay) {
                dayExcess = true;
                filteredMealsPerDay.forEach(meal -> meal.setExcess(true));
            }
        }
    }
}
