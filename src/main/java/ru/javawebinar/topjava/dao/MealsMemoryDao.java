package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MealsMemoryDao implements MealsDao {
    private static final AtomicInteger mealCount = new AtomicInteger();

    private final Map<Integer, Meal> meals = new ConcurrentHashMap<>();

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }

    @Override
    public void delete(int id) {
        meals.remove(id);
    }

    @Override
    public Meal add(Meal meal) {
        int id;
        meal.setId(id = mealCount.incrementAndGet());
        return meals.merge(id, meal, (old, current) -> current);
    }

    @Override
    public Meal update(Meal meal) {
        return meals.computeIfPresent(meal.getId(), (k, v) -> meal);
    }

    @Override
    public Meal get(int id) {
        Meal meal = meals.get(id);
        return (meal == null) ? null : new Meal(id, meal.getDateTime(), meal.getDescription(), meal.getCalories());
    }
}