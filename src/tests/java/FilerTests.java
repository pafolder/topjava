import org.junit.Test;
import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static ru.javawebinar.topjava.util.UserMealsUtil.*;

public class FilerTests {
    private final int MEALS_NUMBER = 1000;
    private final int NUMBER_OF_SEARCHES = 10000;


    @Test
    public void PerformanceTest() {
        int i;
        List<UserMeal> meals = new ArrayList<>();
        meals.add(new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        meals.add(new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        meals.add(new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        meals.add(new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        meals.add(new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        meals.add(new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        meals.add(new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
        for (i = 0; i < MEALS_NUMBER; i++) {
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
        start = System.currentTimeMillis();
        for (i = 0; i < NUMBER_OF_SEARCHES; i++) {
            mealsFilteredByCycles = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        }
        end = System.currentTimeMillis();
        System.out.format(" %d ms", end - start);

        System.out.print("\nFiltered by Streams:");
        start = System.currentTimeMillis();
        for (i = 0; i < NUMBER_OF_SEARCHES; i++) {
            mealsFilteredByStreams = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        }
        end = System.currentTimeMillis();
        System.out.format(" %d ms", end - start);

        System.out.print("\nFiltered by Recursion:");
        start = System.currentTimeMillis();
        for (i = 0; i < NUMBER_OF_SEARCHES; i++) {
            mealsFilteredByRecursion = filteredByRecursion(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        }
        end = System.currentTimeMillis();
        System.out.format(" %d ms\n", end - start);
        assertEquals(mealsFilteredByStreams.size(), mealsFilteredByCycles.size());
        assertEquals(mealsFilteredByStreams.size(), mealsFilteredByRecursion.size());
    }
}
