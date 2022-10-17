package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
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
        return getAllFilteredByUserIdAndSortedByDateTime(repository.values().stream(), userId)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Meal> getAllFilteredByDate(LocalDate _startDate, LocalDate _endDate, int userId) {
        LocalDate startDate = _startDate == null ? null : _startDate.minus(Period.ofDays(1));
        LocalDate endDate = _endDate == null ? null : _endDate.plus(Period.ofDays(1));
        return getAllFilteredByUserIdAndSortedByDateTime(repository.values().stream(), userId)
                .filter(startDate != null ? meal -> meal.getDateTime().toLocalDate().isAfter(startDate) : meal -> true)
                .filter(endDate != null ? meal -> meal.getDateTime().toLocalDate().isBefore(endDate) : meal -> true)
                .collect(Collectors.toList());
    }

    private Stream<Meal> getAllFilteredByUserIdAndSortedByDateTime(Stream<Meal> stream, int userId) {
        return stream
                .filter(meal -> meal.getUserId() == userId)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed());
    }
}