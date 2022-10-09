package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MealsMemoryDao implements MealsDao {
    static AtomicInteger mealCount = new AtomicInteger();


    private final CopyOnWriteArrayList<Meal> meals = new CopyOnWriteArrayList<>();

    public MealsMemoryDao() {
        meals.addAll(Arrays.asList(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500, mealCount.addAndGet(1)),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000, mealCount.addAndGet(1)),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500, mealCount.addAndGet(1)),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100, mealCount.addAndGet(1)),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000, mealCount.addAndGet(1)),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500, mealCount.addAndGet(1)),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410, mealCount.addAndGet(1))
        ));
    }

    @Override
    public List<Meal> getAll() {
        return meals;
    }

    @Override
    public void delete(Integer id) {
        meals.remove(meals.stream().filter(meal -> meal.getId().equals(id)).findAny().orElse(null));
    }

    public Integer add(Meal meal) {
        if (meal.getId() == 0) {
            meal.setId(mealCount.addAndGet(1));
        }
        meals.add(meal);
        return meal.getId();
    }

    @Override
    public void update(Meal meal) {
        delete(meal.getId());
        add(meal);
    }

    @Override
    public Meal get(Integer id) {
        return meals.stream().filter(meal -> meal.getId().equals(id)).findAny().orElse(null);
    }
}