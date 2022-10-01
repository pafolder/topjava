package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class CustomCollector implements Collector<UserMeal, List<UserMeal>, List<UserMealWithExcess>> {
    private final Map<LocalDate, Integer> caloriesByDate = new HashMap<>();
    private final int caloriesPerDay;

    public CustomCollector(int caloriesPerDay) {
        this.caloriesPerDay = caloriesPerDay;
    }

    public void addCaloriesToDate(UserMeal userMeal) {
        caloriesByDate.put(userMeal.getDateTime().toLocalDate(), caloriesByDate.getOrDefault(userMeal.getDateTime().toLocalDate(), 0) + userMeal.getCalories());
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
            mealList.forEach(userMeal -> resultList.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(),
                            caloriesByDate.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay)));
            return resultList;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.singleton(Characteristics.UNORDERED);
    }
}
