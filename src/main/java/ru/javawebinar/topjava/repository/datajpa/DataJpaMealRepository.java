package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.model.UserWithMeals;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Profile("datajpa")
public class DataJpaMealRepository implements MealRepository {

    private final CrudMealRepository crudRepository;
    private final CrudUserRepository crudUserRepository;

    @Autowired
    public DataJpaMealRepository(CrudMealRepository crudRepository, CrudUserRepository crudUserRepository) {
        this.crudRepository = crudRepository;
        this.crudUserRepository = crudUserRepository;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        User user = crudUserRepository.getReferenceById(userId);
        meal.setUser(user);
        if (!meal.isNew()) {
            if (get(meal.id(), userId) == null) {
                return null;
            }
        }
        crudRepository.save(meal);
        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        Meal meal = get(id, userId);
        if (meal == null) {
            return false;
        }
        crudRepository.delete(meal);
        return true;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = crudRepository.findById(id).orElse(null);
        if (meal == null || meal.getUser().getId() != userId) {
            return null;
        }
        return meal;
    }

    @Override
    public List<Meal> getAll(int userId) {
//        return crudRepository.findAll(Sort.by(Sort.Direction.DESC, "dateTime"));
        return crudRepository.getAllByUserId(userId).stream().sorted(Comparator.comparing(Meal::getDateTime).reversed()).collect(Collectors.toList());
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return crudRepository.getBetween(startDateTime, endDateTime, userId);
    }

    @Override
    public UserWithMeals getUserWithMeals(int userId) {
        var userWithMeals = new UserWithMeals();
        userWithMeals.setMeals(crudRepository.getUserWithMeals(userId));
        if (userWithMeals.getMeals() != null && userWithMeals.getMeals().size() != 0) {
            userWithMeals.setUser(userWithMeals.getMeals().get(0).getUser());
        }
//        userWithMeals.setUser(crudUserRepository.findById(userId).orElse(null));
        return userWithMeals;
    }
}
