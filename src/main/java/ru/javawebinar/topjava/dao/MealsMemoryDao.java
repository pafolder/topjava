package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MealsMemoryDao implements MealsDao {
    static final AtomicInteger mealCount = new AtomicInteger();

    private final Map<Integer, Meal> meals = new HashMap<>();


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
        meal.setId(mealCount.incrementAndGet());
        meals.put(meal.getId(), meal);
        return meal;
    }

    @Override
    public Meal update(Meal meal) {
        return (meal.getId() == null) ? add(meal) : meals.put(meal.getId(), meal);
    }

    @Override
    public Meal get(int id) {
        return meals.get(id);
    }
}