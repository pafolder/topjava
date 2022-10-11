package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealsDao {
    List<Meal> getAll();

    void delete(int id);

    Meal save(Meal meal);

    Meal get(int id);
}
