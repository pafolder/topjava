package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.formatter.LocalDate.LocalDateFormat;
import ru.javawebinar.topjava.formatter.LocalTime.LocalTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping(value = MealRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class MealRestController extends AbstractMealController {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    static final String REST_URL = "/rest/meals";

    @GetMapping
    public List<MealTo> getAll() {
        return super.getAll();
    }

    @GetMapping("/{id}")
    public Meal get(@PathVariable("id") Integer id) {
        return super.get(id);
    }

    @GetMapping("/filter")
//    public List<MealTo> getBetween(@RequestParam
//                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//                                       LocalDateTime start,
//                                   @RequestParam
//                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//                                   LocalDateTime end) {
    public List<MealTo> getBetween(
            @RequestParam
            @LocalDateFormat
            LocalDate startDate,
            @RequestParam
            @LocalTimeFormat
            LocalTime startTime,
            @RequestParam
            @LocalDateFormat
            LocalDate endDate,
            @RequestParam
            @LocalTimeFormat
            LocalTime endTime
    ) {
        return super.getBetween(
                startDate == LocalDate.MIN ? null : startDate,
                startTime == LocalTime.MIN ? null : startTime,
                endDate == LocalDate.MIN ? null : endDate,
                endTime == LocalTime.MIN ? null : endTime
        );
    }

    @PostMapping
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Meal put(@RequestBody Meal meal) {
        Meal resultingMeal;
        if (meal.isNew()) {
            resultingMeal = super.create(meal);
        } else {
            super.update(meal, meal.getId());
            resultingMeal = meal;
        }
        return resultingMeal;
    }

    @DeleteMapping("/{mealId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer mealId) {
        super.delete(mealId);
    }
}