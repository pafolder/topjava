package ru.javawebinar.topjava.model;

import java.util.List;

public class UserWithMeals {
    private User user;
    private List<Meal> meals;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public UserWithMeals(User user, List<Meal> meals) {
        this.user = user;
        this.meals = meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }
}
