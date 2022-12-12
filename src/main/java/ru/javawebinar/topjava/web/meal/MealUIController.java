package ru.javawebinar.topjava.web.meal;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealFilterTo;
import ru.javawebinar.topjava.to.MealTo;

import java.util.List;

import static ru.javawebinar.topjava.util.MealsUtil.createFromTo;

@RestController
@RequestMapping(value = "/profile/meals", produces = MediaType.APPLICATION_JSON_VALUE)
public class MealUIController extends AbstractMealController {

    @Override
    @GetMapping
    public List<MealTo> getAll() {
        return super.getAll();
    }

    @Override
    @GetMapping("/{id}")
    public Meal get(@PathVariable int id) {
        return super.get(id);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createOrUpdate(MealTo mealTo) {
        Meal meal = createFromTo(mealTo);
        if (meal.isNew()) {
            super.create(meal);
        } else {
            super.update(meal, meal.getId());
        }
    }

    @GetMapping("/filter")
    public List<MealTo> getBetween(MealFilterTo filter) {
        return super.getBetween(filter.getStartDate(), filter.getStartTime(), filter.getEndDate(), filter.getEndTime());
    }
}