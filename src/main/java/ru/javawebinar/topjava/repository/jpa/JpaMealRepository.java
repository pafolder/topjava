package ru.javawebinar.topjava.repository.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class JpaMealRepository implements MealRepository {
    @PersistenceContext
    private EntityManager em;

    Logger log = LoggerFactory.getLogger(JpaMealRepository.class);

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setUser(em.getReference(User.class, userId));
            em.persist(meal);
            return meal;
        } else {
            return em.createQuery("UPDATE Meal m SET m.dateTime =:dateTime, " +
                            "m.description=:description, m.calories=:calories" +
                            " WHERE m.id=:id AND m.user.id=:userId")
                    .setParameter("dateTime", meal.getDateTime())
                    .setParameter("description", meal.getDescription())
                    .setParameter("calories", meal.getCalories())
                    .setParameter("id", meal.getId())
                    .setParameter("userId", userId)
                    .executeUpdate() == 0 ? null : meal;
        }
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        log.debug("Hello from delete");
        return em.createQuery("DELETE FROM Meal m WHERE m.id=:id AND m.user.id=:userId")
                .setParameter("id", id)
                .setParameter("userId", userId)
                .executeUpdate() != 0;

    }

    @Override
    public Meal get(int id, int userId) {
        Meal foundMeal = em.find(Meal.class, id);
        if (foundMeal == null || foundMeal.getUser().getId() != userId) {
            return null;
        }
        foundMeal.setUser(null);
        return foundMeal;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return em.createQuery("SELECT m FROM Meal m WHERE m.user.id =:userId ORDER BY m.dateTime DESC",
                        Meal.class).setParameter("userId", userId).getResultList().stream()
                .peek(meal -> meal.setUser(null)).collect(Collectors.toList());
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return em.createQuery("SELECT m FROM Meal m WHERE m.user.id =:userId AND m.dateTime >= :startDateTime" +
                        " AND m.dateTime < :endDateTime ORDER BY m.dateTime DESC", Meal.class)
                .setParameter("userId", userId).setParameter("startDateTime", startDateTime)
                .setParameter("endDateTime", endDateTime).getResultList().stream()
                .peek(meal -> meal.setUser(null)).collect(Collectors.toList());
    }
}