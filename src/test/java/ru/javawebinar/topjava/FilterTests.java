package ru.javawebinar.topjava;

import org.openjdk.jmh.annotations.*;
import ru.javawebinar.topjava.model.UserMeal;
import org.openjdk.jmh.infra.Blackhole;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static ru.javawebinar.topjava.util.UserMealsUtil.*;

@State(Scope.Benchmark)
public class FilterTests {
    @Param({"500000", "1000000", "2000000", "4000000", "8000000"})
    private int MEALS_NUMBER = 1000000;
    private List<UserMeal> meals = new ArrayList<>();

    @Setup(Level.Iteration)
    public void prepareMeals() {
        for (int i = 0; i < MEALS_NUMBER; i++) {
            meals.add(new UserMeal(LocalDateTime.of(i % 2000,
                    1 + i % 12,
                    1 + i % 27,
                    i % 24,
                    i % 60),
                    "Automatically generated meal",
                    (int) (Math.random() * 3000)));
        }
    }


    @Fork(value = 1, warmups = 1, jvmArgs = "-Xmx1000m")
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void cyclesPerformance(Blackhole blackhole) {
        blackhole.consume(filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }
}
