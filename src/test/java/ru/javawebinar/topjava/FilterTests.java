package ru.javawebinar.topjava;

import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static ru.javawebinar.topjava.util.UserMealsUtil.*;

@State(Scope.Benchmark)
public class FilterTests {
    private final int MEALS_NUMBER = 1000;
    private final int NUMBER_OF_ITERATIONS = 10000;
    private final List<UserMeal> meals = new ArrayList<>();

    {
        for (int i = 0; i < MEALS_NUMBER; i++) {
            meals.add(new UserMeal(LocalDateTime.of(2000 + (int) (Math.random() * 22),
                    1 + (int) (Math.random() * 11),
                    1 + (int) (Math.random() * 28),
                    (int) (Math.random() * 23),
                    (int) (Math.random() * 59)),
                    "Randomly generated meal",
                    (int) (Math.random() * 3000)));
        }
    }

    @Test
    public void performanceTest() {
        List<UserMeal> meals = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            meals.add(new UserMeal(LocalDateTime.of(2000 + (int) (Math.random() * 22),
                    1 + (int) (Math.random() * 11),
                    1 + (int) (Math.random() * 28),
                    (int) (Math.random() * 23),
                    (int) (Math.random() * 59)),
                    "Randomly generated meal",
                    (int) (Math.random() * 3000)));
        }

        List<UserMealWithExcess> mealsFilteredByCycles = null;
        List<UserMealWithExcess> mealsFilteredByStreams = null;
        List<UserMealWithExcess> mealsFilteredByRecursion = null;
        long start;
        long end;
        System.out.print("\nFiltered by Cycles:");
        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            mealsFilteredByCycles = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        }
        end = System.nanoTime();
        System.out.format(" %d ms", (end - start) / 1000000);

        System.out.print("\nFiltered by Streams:");
        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            mealsFilteredByStreams = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        }
        end = System.nanoTime();
        System.out.format(" %d ms", (end - start) / 1000000);

        System.out.print("\nFiltered by Recursion:");
        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            mealsFilteredByRecursion = filteredByRecursion(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        }
        end = System.nanoTime();
        System.out.format(" %d ms\n", (end - start) / 1000000);
        assertEquals(mealsFilteredByStreams.size(), mealsFilteredByCycles.size());
        assertEquals(mealsFilteredByStreams.size(), mealsFilteredByRecursion.size());
    }

    //    @Fork(value = 1, warmups = 1)
    @Benchmark
    @Warmup(iterations = 1)
    @BenchmarkMode(Mode.SingleShotTime)
    public void cyclesPerformance() {
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        }
    }

    @Benchmark
    @Warmup(iterations = 1)
    @BenchmarkMode(Mode.SingleShotTime)
    public void streamsPerformance() {
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        }
    }

    @Benchmark
    @Warmup(iterations = 1)
    @BenchmarkMode(Mode.SingleShotTime)
    public void recursionPerformance() {
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            filteredByRecursion(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        }
    }
}
