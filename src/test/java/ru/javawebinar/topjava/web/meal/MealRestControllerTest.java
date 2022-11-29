package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.SecurityUtil;
import ru.javawebinar.topjava.web.json.JsonUtil;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_ID;


class MealRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = MealRestController.REST_URL + '/';
    @Autowired
    MealService mealService;

    @Test
    void create() throws Exception {
        Meal meal = MealTestData.getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(meal)))
                .andExpect(status().isCreated())
                .andDo(print());
        Meal created = MEAL_MATCHER.readFromJson(action);
        int newId = created.id();
        Meal readMeal = mealService.get(newId, SecurityUtil.authUserId());
        meal.setId(newId);
        MEAL_MATCHER.assertMatch(created, meal);
        MEAL_MATCHER.assertMatch(readMeal, meal);
    }

    @Test
    void update() throws Exception {
        int id = getUpdated().getId();
        perform(MockMvcRequestBuilders.put(REST_URL + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdated())))
                .andExpect(status().isOk())
                .andDo(print());
        Meal readMeal = mealService.get(id, SecurityUtil.authUserId());
        MEAL_MATCHER.assertMatch(readMeal, getUpdated());
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + MEAL1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> mealService.get(MEAL1_ID, USER_ID));
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEALTO_MATCHER.contentJson(mealsTo));
    }

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + MEAL1_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_MATCHER.contentJson(meal1));
    }

    @Test
    void getBetween() throws Exception {

        perform(MockMvcRequestBuilders.get(REST_URL + "/filter?")
                .param("startDate", "2020-01-30")
                .param("endDate", "2020-01-30")
                .param("startTime", "00:00:00")
                .param("endTime", "23:00:00")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEALTO_MATCHER.contentJson(mealsTo.get(4), mealsTo.get(5), mealsTo.get(6)));
    }

    @Test
    void getBetweenWithEmptyAndNull() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/filter?")
                .param("startDate", "")
                .param("endDate", "")
                .param("startTime", "")
                .param("endTime", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEALTO_MATCHER.contentJson(mealsTo));
    }
}