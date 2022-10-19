package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final Map<Integer, Set<Integer>> userMap = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

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
            userMap.computeIfAbsent(userId, k -> new ConcurrentSkipListSet<>());
            userMap.get(userId).add(meal.getId());
            return meal;
        }
        if (repository.get(meal.getId()) == null || repository.get(meal.getId()).getUserId() != userId) {
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
        userMap.get(userId).remove(meal.getId());
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
        List<Meal> mealsOfUser = new ArrayList<>();
        userMap.get(userId).forEach(mealId -> mealsOfUser.add(repository.get(mealId)));
        return mealsOfUser;
    }

    @Override
    public List<Meal> getAllFilteredByDate(LocalDate startDate, LocalDate endDate, int userId) {
        return filteredByPredicate(getAll(userId), meal -> DateTimeUtil.isBetweenDates(meal.getDate(), startDate, endDate)).stream()
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    private List<Meal> filteredByPredicate(Collection<Meal> meals, Predicate<Meal> filter) {
        return meals.stream().
                filter(filter).
                collect(Collectors.toList());
    }
}