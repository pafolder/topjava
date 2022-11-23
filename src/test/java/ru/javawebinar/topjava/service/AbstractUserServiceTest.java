package ru.javawebinar.topjava.service;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.JpaUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.UserTestData.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public abstract class AbstractUserServiceTest extends AbstractServiceTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    protected UserService service;

    @Autowired(required = false)
    private CacheManager cacheManager;

    @Autowired(required = false)
    protected JpaUtil jpaUtil;

    @Before
    public void setup() {
        if (!isJDBC()) {
            cacheManager.getCache("users").clear();
            jpaUtil.clear2ndLevelHibernateCache();
        }
    }

    @Test
    public void create() {
        User created = service.create(getNew());
        int newId = created.id();
        User newUser = getNew();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(service.get(newId), newUser);
    }

    @Test
    public void create2Role() {
        User created = service.create(getNew2Role());
        int newId = created.id();
        User newUser = getNew2Role();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(service.get(newId), newUser);
    }

    @Test
    public void duplicateMailCreate() {
        assertThrows(DataAccessException.class, () ->
                service.create(new User(null, "Duplicate", "user@yandex.ru", "newPass", Role.USER)));
    }

    @Test
    public void delete() {
        service.delete(USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(USER_ID));
    }

    @Test
    public void deletedNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND));
    }

    @Test
    public void get() {
        User user = service.get(USER_ID);
        USER_MATCHER.assertMatch(user, UserTestData.user);
    }

    @Test
    public void get2Roles() {
        User user = service.get(ADMIN_ID);
        USER_MATCHER.assertMatch(user, admin);
    }

    @Test
    public void getWithMeals() {
        User user = service.getWithMeals(USER_ID);
        USER_MATCHER.assertMatch(user, UserTestData.user);
    }

    @Test
    public void getWithMeals2Roles() {
        User user = service.getWithMeals(ADMIN_ID);
        USER_MATCHER.assertMatch(user, admin);
        Set<Meal> actual = user.getMeals();
        Set<Meal> expected = Set.of(MealTestData.adminMeal1, MealTestData.adminMeal2);
        MealTestData.MEAL_MATCHER.assertMatch(actual, expected);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND));
    }

    @Test
    public void getByEmail() {
        User user = service.getByEmail("admin@gmail.com");
        USER_MATCHER.assertMatch(user, admin);
    }

    @Test
    public void a_update() {
        User updated = getUpdated();
        service.update(updated);
//        USER_MATCHER.assertMatch(service.get(USER_ID), getUpdated());
        USER_MATCHER.assertMatch(service.getAll(), updated, admin, guest );
    }

    @Test
    public void update2Roles() {
        User updated2Role = getUpdated2Role();
        service.update(getUpdated2Role());
        USER_MATCHER.assertMatch(service.get(ADMIN_ID), getUpdated2Role());
    }

    @Test
    public void getAll() {
        List<User> all = service.getAll();
        USER_MATCHER.assertMatch(all, admin, guest, user);
    }

    @Test
    public void createWithException() {
        validateRootCause(ConstraintViolationException.class, () -> service.create(new User(null, "  ", "mail@yandex.ru", "password", Role.USER)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new User(null, "User", "  ", "password", Role.USER)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new User(null, "User", "mail@yandex.ru", "  ", Role.USER)));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new User(null, "User", "mail@yandex.ru", "password", 9, true, new Date(), Set.of())));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new User(null, "User", "mail@yandex.ru", "password", 10001, true, new Date(), Set.of())));
    }
}