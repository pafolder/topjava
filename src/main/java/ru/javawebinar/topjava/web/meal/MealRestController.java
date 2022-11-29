package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.ValidationUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping(value = MealRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class MealRestController extends AbstractMealController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    static final String REST_URL = "/rest/meals";

    @GetMapping
    @Override
    public List<MealTo> getAll() {
        return super.getAll();
    }

    @GetMapping("/{id}")
    @Override
    public Meal get(@PathVariable("id") int id) {
        return super.get(id);
    }

    @GetMapping("/filter")
    @Override
    public List<MealTo> getBetween(@RequestParam @Nullable LocalDate startDate, @RequestParam @Nullable LocalTime startTime,
                                   @RequestParam @Nullable LocalDate endDate, @RequestParam @Nullable LocalTime endTime) {
        return super.getBetween(startDate, startTime, endDate, endTime);
    }

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public Meal create(@RequestBody Meal meal) {
        return super.create(meal);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@PathVariable int id, @RequestBody Meal meal) {
        ValidationUtil.assureIdConsistent(meal, id);
        super.update(meal, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void delete(@PathVariable int id) {
        super.delete(id);
    }
}