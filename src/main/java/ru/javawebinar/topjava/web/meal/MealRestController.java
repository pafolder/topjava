package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class MealRestController extends AbstractMealController {
    public List<Meal> getAll() {
        return super.getAll();
    }

    public List<MealTo> getAllTosFilteredByDate(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        return super.getAllTosFilteredByDate(startDate, endDate, startTime, endTime);
    }

    public Meal get(int id) {
        return super.get(id);
    }

    public void delete(int id) {
        super.delete(id);
    }

    public Meal update(Meal meal, int id) {
        return super.update(meal, id);
    }

    public Meal create(Meal meal) {
        return super.create(meal);
    }
}