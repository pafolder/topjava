package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

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

        System.out.println("Filtered by Cycles:");
        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println("Filtered by Streams with MealsCollector:");
        List<UserMealWithExcess> mealsToOptional = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsToOptional.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDate = new HashMap<>();
        meals.forEach(userMeal -> {
            LocalDate date = userMeal.getDateTime().toLocalDate();
            caloriesByDate.put(date, caloriesByDate.getOrDefault(date, 0) + userMeal.getCalories());
        });
        List<UserMealWithExcess> listWithExcess = new ArrayList<>();
        meals.forEach(userMeal -> {
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                listWithExcess.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(),
                        caloriesByDate.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay));
            }
        });
        return listWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        MealsCollector mealsCollector = new MealsCollector(startTime, endTime, caloriesPerDay);
        return meals.stream()
                .map(mealsCollector::addMeal)
                .collect(mealsCollector);
    }

    static class MealsCollector implements Collector<UserMeal, List<UserMeal>, List<UserMealWithExcess>> {
        static class FilteredMealsPerDayWithTotalCarlories {
            public List<UserMeal> filteredMealsPerDay = new ArrayList<>();
            public int totalCaloriesPerDay = 0;
        }

        private final Map<LocalDate, FilteredMealsPerDayWithTotalCarlories> fMealsPerDate = new HashMap<>();
        private final int caloriesPerDay;
        private final LocalTime startTime;
        private final LocalTime endTime;

        public MealsCollector(LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.caloriesPerDay = caloriesPerDay;
        }

        public UserMeal addMeal(UserMeal userMeal) {
            FilteredMealsPerDayWithTotalCarlories fMealsPerDay = fMealsPerDate.getOrDefault(userMeal.getDateTime().toLocalDate(),
                    new FilteredMealsPerDayWithTotalCarlories());
            fMealsPerDay.totalCaloriesPerDay += userMeal.getCalories();
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                fMealsPerDay.filteredMealsPerDay.add(userMeal);
            }
            fMealsPerDate.put(userMeal.getDateTime().toLocalDate(), fMealsPerDay);
            return userMeal;
        }

        @Override
        public Supplier<List<UserMeal>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<UserMeal>, UserMeal> accumulator() {
            return List::add;
        }

        @Override
        public BinaryOperator<List<UserMeal>> combiner() {
            return (left, right) -> {
                left.addAll(right);
                return left;
            };
        }

        @Override
        public Function<List<UserMeal>, List<UserMealWithExcess>> finisher() {
            List<UserMealWithExcess> resultList = new ArrayList<>();
            return (mealList) -> {
                fMealsPerDate.forEach((date, fMealsPerDay) ->
                        fMealsPerDay.filteredMealsPerDay.forEach(userMeal -> resultList.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(),
                                fMealsPerDay.totalCaloriesPerDay > caloriesPerDay)))
                );
                return resultList;
            };
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.singleton(Characteristics.UNORDERED);
        }
    }
}
