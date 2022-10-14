package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;

import java.util.List;

@Controller
public class MealRestController extends AbstractMealController {
    public List<Meal> getAll() {
        return super.getAll();
    }

    public Meal get(int id) {
        return super.get(id);
    }

    public void delete(int id) {
        super.delete(id);
    }

    public Meal update(Meal meal) {
        return super.update(meal);
    }
}