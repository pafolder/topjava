package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealsDao {
    List<Meal> getAll();

    void delete(Integer id);

    Meal add(Meal meal);

    Meal update(Meal meal);

    Meal get(Integer id);
}
