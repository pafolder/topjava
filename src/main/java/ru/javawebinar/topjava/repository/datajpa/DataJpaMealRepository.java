package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.model.UserWithMeals;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Profile(Profiles.DATAJPA)
public class DataJpaMealRepository implements MealRepository {

    private final CrudMealRepository crudRepository;
    private final CrudUserRepository crudUserRepository;

    @Autowired
    public DataJpaMealRepository(CrudMealRepository crudRepository, CrudUserRepository crudUserRepository) {
        this.crudRepository = crudRepository;
        this.crudUserRepository = crudUserRepository;
    }

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        User user = crudUserRepository.getReferenceById(userId);
        meal.setUser(user);
        if (!meal.isNew() && get(meal.id(), userId) == null) {
            return null;
        }
        crudRepository.save(meal);
        return meal;
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        return crudRepository.deleteMeal(id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        return crudRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudRepository.findByUserId(userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return crudRepository.findByDateTimeIsAfterAndDateTimeIsBeforeAndUserIdOrderByDateTimeDesc(startDateTime, endDateTime, userId);
    }

    @Override
    public UserWithMeals getUserWithMeals(int userId) {
        User user = crudUserRepository.findById(userId).orElse(null);
        return user == null ? null : new UserWithMeals(user, getAll(userId));
    }
}
