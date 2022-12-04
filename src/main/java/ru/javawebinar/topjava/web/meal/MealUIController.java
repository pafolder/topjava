package ru.javawebinar.topjava.web.meal;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/ui/meals")
public class MealUIController extends AbstractMealController {
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createMeal(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime, @RequestParam String description,
                           @RequestParam int calories) {
        super.create(new Meal(dateTime, description, calories));
    }

    @GetMapping
    public List<MealTo> getMeals(Model model) {
        return super.getAll();
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

}
