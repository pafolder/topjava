package ru.javawebinar.topjava.service;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.basic.AbstractMealServiceTest;

@ActiveProfiles({"jpa", "postgres"})
public class JpaMealServiceTest extends AbstractMealServiceTest {
}