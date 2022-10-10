package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MealsMemoryDao implements MealsDao {
    static final AtomicInteger mealCount = new AtomicInteger();

    private final ConcurrentMap<Integer, Meal> meals = new ConcurrentHashMap<>();

    @Override
    public void addTestData() {
        meals.put(mealCount.incrementAndGet(), new Meal(mealCount.get(), LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        meals.put(mealCount.incrementAndGet(), new Meal(mealCount.get(), LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        meals.put(mealCount.incrementAndGet(), new Meal(mealCount.get(), LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        meals.put(mealCount.incrementAndGet(), new Meal(mealCount.get(), LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        meals.put(mealCount.incrementAndGet(), new Meal(mealCount.get(), LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        meals.put(mealCount.incrementAndGet(), new Meal(mealCount.get(), LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        meals.put(mealCount.incrementAndGet(), new Meal(mealCount.get(), LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }

    @Override
    public void delete(int id) {
        synchronized (mealCount) {
            meals.remove(id);
        }
    }

    @Override
    public Meal add(Meal meal) {
        synchronized (mealCount) {
            meal.setId(mealCount.incrementAndGet());
            meals.putIfAbsent(meal.getId(), meal);
        }
        return meal;
    }

    @Override
    public Meal update(Meal meal) {
        synchronized (mealCount) {
            delete(meal.getId());
            meals.put(meal.getId(), meal);
        }
        return meal;
    }

    @Override
    public Meal get(int id) {
        return meals.get(id);
    }
}