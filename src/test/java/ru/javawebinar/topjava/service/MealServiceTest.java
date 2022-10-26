package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.assertMatch;
import static ru.javawebinar.topjava.MealTestData.getNew;
import static ru.javawebinar.topjava.MealTestData.getUpdated;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {
    @org.springframework.context.annotation.Configuration
    @ComponentScan("ru.javawebinar.topjava")
    public static class ContextConfiguration {
        @Bean
        public DataSource dataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setUrl("jdbc:postgresql://localhost:5432/topjava");
            dataSource.setUsername("user");
            dataSource.setPassword("password");
            return dataSource;
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }
    }

    @Autowired
    ConfigurableEnvironment env;

    public MealServiceTest() {
//        env.setActiveProfiles("inmemory");
    }

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(MEAL_START_ID, USER_ID);
        assertMatch(meal, TEST_MEAL_1);
    }

    @Test
    public void getMealOfOtherUser() {
        assertThrows(NotFoundException.class, () -> service.get(MEAL_START_ID, GUEST_ID));
    }

    @Test
    public void getMealNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void getUserNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, NOT_FOUND));
    }

    @Test
    public void delete() {
        service.delete(MEAL_START_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(MEAL_START_ID, USER_ID));
    }

    @Test
    public void deleteMealOfOtherUser() {
        assertThrows(NotFoundException.class, () -> service.delete(MEAL_START_ID, GUEST_ID));
    }

    @Test
    public void deleteMealNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER_ID));
    }

    @Test
    public void deleteUserNotFound() {
        service.delete(MEAL_START_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(MEAL_START_ID, NOT_FOUND));
    }

    @Test
    public void getAll() {
        List<Meal> all = service.getAll(USER_ID);
        MealTestData.assertMatch(all, mealsSortedNewToOld);
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> filteredMeals = service.getBetweenInclusive(START_DATE, END_DATE, USER_ID);
        assertMatch(filteredMeals, oneDayMeals);
    }

    @Test
    public void getBetweenInclusiveNullNull() {
        List<Meal> filteredMeals = service.getBetweenInclusive(null, null, USER_ID);
        assertEquals(mealsSortedNewToOld, filteredMeals);
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.get(MEAL_START_ID, USER_ID), getUpdated());
    }

    @Test
    public void updateUserNotFound() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdated(), NOT_FOUND));
    }

    @Test
    public void updateMealsOfOtherUsers() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdated(), ADMIN_ID));
        assertThrows(NotFoundException.class, () -> service.update(getUpdated(), GUEST_ID));
    }

    @Test
    public void create() {
        Meal created = service.create(getNew(), USER_ID);
        Integer newId = created.getId();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void createForSameDateTime() {
        service.create(getNew(), USER_ID);
        assertThrows(DuplicateKeyException.class, () -> service.create(getNew(), USER_ID));
    }

}
