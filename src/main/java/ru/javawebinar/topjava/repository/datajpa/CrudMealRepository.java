package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

public interface CrudMealRepository extends JpaRepository<Meal, Integer> {
    List<Meal> getAllByUserId(int userId);

    @Query("SELECT m FROM  Meal m WHERE m.dateTime >= ?1 AND m.dateTime < ?2 AND m.user.id = ?3 ORDER BY m.dateTime DESC")
    List<Meal> getBetween(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId);

    @Query("SELECT m FROM Meal m WHERE m.user.id = ?1 ORDER BY m.dateTime DESC")
    List<Meal> getUserWithMeals(int userId);
}
