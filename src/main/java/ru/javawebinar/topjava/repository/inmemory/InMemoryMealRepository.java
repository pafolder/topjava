package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private static final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.mealsForUser1.forEach(meal -> save(meal, 1));
        MealsUtil.mealsForUser2.forEach(meal -> save(meal, 2));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            repository.put(meal.getId(), meal);
            return meal;
        }

        if (meal.getUserId() != userId) {
            return null;
        }
        // handle case: update, but not present in storage
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        Meal meal = repository.get(id);
        if (meal == null || meal.getUserId() != userId) {
            return false;
        }
        return repository.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = repository.get(id);
        if (meal == null || meal.getUserId() != userId) {
            return null;
        }
        return meal;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return repository.values().stream()
                .filter(meal -> meal.getUserId() == userId)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Meal> getAllFilteredByDate(LocalDate startDate, LocalDate endDate, int userId) {
        return repository.values().stream()
                .filter(meal -> meal.getUserId() == userId)
                .filter(startDate != null ? meal -> meal.getDateTime().toLocalDate().isAfter(startDate.minus(Period.ofDays(1))) : meal -> true)
                .filter(endDate != null ? meal -> meal.getDateTime().toLocalDate().isBefore(endDate.plus(Period.ofDays(1))) : meal -> true)
                .sorted(Collections.reverseOrder(Comparator.comparing(Meal::getDateTime)))
                .collect(Collectors.toList());
    }

}

