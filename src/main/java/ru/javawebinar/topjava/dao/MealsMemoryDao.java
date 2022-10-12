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
        synchronized (meals) {
            meals.remove(id);
        }
    }

    @Override
    public Meal save(Meal meal) {
        Integer id = meal.getId();
        if (id == null) {
            meal.setId(id = mealCount.incrementAndGet());
        }
        synchronized (meals) {
            meals.put(id, meal);
            return meals.get(id);
        }
    }

    @Override
    public Meal get(int id) {
        Meal meal = meals.get(id);
        return (meal == null) ? null : new Meal(id, meal.getDateTime(), meal.getDescription(), meal.getCalories());
    }
}