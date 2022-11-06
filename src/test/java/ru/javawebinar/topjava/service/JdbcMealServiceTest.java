package ru.javawebinar.topjava.service;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.basic.AbstractMealServiceTest;

@ActiveProfiles({"jdbc", "hsqldb"})
public class JdbcMealServiceTest extends AbstractMealServiceTest {
}
