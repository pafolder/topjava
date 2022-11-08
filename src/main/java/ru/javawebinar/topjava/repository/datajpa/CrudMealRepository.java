package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface CrudMealRepository extends JpaRepository<Meal, Integer> {

    @Query("SELECT new Meal(m.id, m.dateTime, m.description, m.calories) FROM  Meal m WHERE m.user.id = ?1 ORDER BY m.dateTime DESC")
    List<Meal> findByUserId(int userId);

//    @Query(value = "SELECT m.user FROM  Meal m WHERE m.id = ?1")
//    User getUserByMealId(int mealId);

//    @Query("SELECT m FROM  Meal m WHERE m.dateTime >= ?1 AND m.dateTime < ?2 AND m.user.id = ?3 ORDER BY m.dateTime DESC")
//    List<Meal> getBetween(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId);

    List<Meal> findByDateTimeIsAfterAndDateTimeIsBeforeAndUserIdOrderByDateTimeDesc(
            LocalDateTime startDateTime, LocalDateTime endDateTime, int userId);

    @Query("SELECT new Meal(m.id, m.dateTime, m.description, m.calories) FROM  Meal m WHERE m.user.id = ?2 AND m.id = ?1")
    Meal findByIdAndUserId(int id, int userId);

    @Transactional
    @Modifying
    @Query("delete from Meal m WHERE m.id = ?1 AND m.user.id = ?2")
    int deleteMeal(int id, int userId);
}