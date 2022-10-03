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
        List<UserMealWithExcess> mealsFilteredByCycles = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsFilteredByCycles.forEach(System.out::println);

        System.out.println("Filtered by Streams Optional 2:");
        List<UserMealWithExcess> mealsFilteredByStreams = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsFilteredByStreams.forEach(System.out::println);

        System.out.println("Filtered by recursion (Bonus):");
        List<UserMealWithExcess> mealsFilteredByRecursion = filteredByRecursion(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsFilteredByRecursion.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByRecursion(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int dayCaloriesLimit) {
        Map<LocalDate, Integer> caloriesByDate = new HashMap<>();
        List<UserMealWithExcess> resultList = new ArrayList<>();
        return recursiveFilter(meals, startTime, endTime, dayCaloriesLimit, caloriesByDate, resultList, meals.size());
    }

    static List<UserMealWithExcess> recursiveFilter(List<UserMeal>meals, LocalTime startTime, LocalTime endTime, int dayCaloriesLimit,
                                                    Map<LocalDate, Integer> caloriesByDate, List<UserMealWithExcess> resultList, int i) {
        if (i != 0) {
            UserMeal userMeal = meals.get(--i);
            caloriesByDate.put(userMeal.getDate(), caloriesByDate.getOrDefault(userMeal.getDate(), 0) + userMeal.getCalories());
            recursiveFilter(meals, startTime, endTime, dayCaloriesLimit, caloriesByDate, resultList, i);
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                resultList.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(),
                        caloriesByDate.get(userMeal.getDate()) > dayCaloriesLimit));
            }
        }
        return resultList;
    }
    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int dayCaloriesLimit) {
        Map<LocalDate, FilteredDayMealsWithDayCaloriesForFilterByCycles> fMealsMap = new HashMap<>();
        List<UserMealWithExcess> resultList = new ArrayList<>();
        meals.forEach(userMeal -> {
            FilteredDayMealsWithDayCaloriesForFilterByCycles fDayMeals = fMealsMap.getOrDefault(userMeal.getDate(),
                    new FilteredDayMealsWithDayCaloriesForFilterByCycles(resultList, dayCaloriesLimit));
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                fDayMeals.addMeal(userMeal);
            } else {
                fDayMeals.addDayCalories(userMeal);
            }
            fMealsMap.put(userMeal.getDate(), fDayMeals);
        });
        return resultList;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int dayCaloriesLimit) {
        return meals.stream()
                .collect(Collectors.toMap(UserMeal::getDate,
                        userMeal -> {
                            FilteredDayMealsWithDayCaloriesForFilterByStreams fMeals = new FilteredDayMealsWithDayCaloriesForFilterByStreams(dayCaloriesLimit);
                            return (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) ?
                                    fMeals.addMeal(userMeal) : fMeals.addDayCalories(userMeal);
                        },
                        (existingDayMeals, nextDayMeals) -> {
                            existingDayMeals.dayCalories += nextDayMeals.dayCalories;
                            if (nextDayMeals.filteredDayMeals.size() != 0)
                                existingDayMeals.filteredDayMeals.add(nextDayMeals.filteredDayMeals.get(0));
                            existingDayMeals.processDayExcess();
                            return existingDayMeals;
                        }
                )).values().stream()
                .map(fDayMeals -> fDayMeals.filteredDayMeals)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    static class FilteredDayMealsWithDayCaloriesForFilterByCycles {
        private final List<Integer> dayIndexesInResultList = new LinkedList<>();
        private final List<UserMealWithExcess> resultListRef;
        private int dayCalories;
        private final int dayCaloriesLimit;
        private boolean dayExcess;

        public FilteredDayMealsWithDayCaloriesForFilterByCycles(List<UserMealWithExcess> resultListRef, int dayCaloriesLimit) {
            this.resultListRef = resultListRef;
            this.dayCaloriesLimit = dayCaloriesLimit;
        }

        private void addMeal(UserMeal userMeal) {
            UserMealWithExcess userMealWithExcess = new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), dayExcess);
            dayIndexesInResultList.add(resultListRef.size());
            resultListRef.add(userMealWithExcess);
            addDayCalories(userMeal);
            processDayExcess();
        }

        public void addDayCalories(UserMeal userMeal) {
            dayCalories += userMeal.getCalories();
            processDayExcess();
        }

        private void processDayExcess() {
            if (!dayExcess && dayCalories > dayCaloriesLimit) {
                dayExcess = true;
                setExcesses(dayIndexesInResultList.size());
            }
        }

        void setExcesses(int i) {
            if (i != 0) {
                resultListRef.get(dayIndexesInResultList.get(--i)).setExcess(true);
                setExcesses(i);
            }
        }
    }

    static class FilteredDayMealsWithDayCaloriesForFilterByStreams {
        private final List<UserMealWithExcess> filteredDayMeals = new ArrayList<>();
        private int dayCalories;
        private final int dayCaloriesLimit;
        private boolean dayExcess;

        public FilteredDayMealsWithDayCaloriesForFilterByStreams(int dayCaloriesLimit) {
            this.dayCaloriesLimit = dayCaloriesLimit;
        }

        private FilteredDayMealsWithDayCaloriesForFilterByStreams addMeal(UserMeal userMeal) {
            UserMealWithExcess userMealWithExcess = new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), dayExcess);
            filteredDayMeals.add(userMealWithExcess);
            addDayCalories(userMeal);
            processDayExcess();
            return this;
        }

        public FilteredDayMealsWithDayCaloriesForFilterByStreams addDayCalories(UserMeal userMeal) {
            dayCalories += userMeal.getCalories();
            processDayExcess();
            return this;
        }

        private void processDayExcess() {
            if (!dayExcess && dayCalories > dayCaloriesLimit) {
                dayExcess = true;
                setExcesses(filteredDayMeals.size());
            }
        }

        void setExcesses(int i) {
            if (i != 0) {
                filteredDayMeals.get(--i).setExcess(true);
                setExcesses(i);
            }
        }

    }
}