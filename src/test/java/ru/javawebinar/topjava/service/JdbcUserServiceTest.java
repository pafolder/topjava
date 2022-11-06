package ru.javawebinar.topjava.service;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.basic.AbstractUserServiceTest;

@ActiveProfiles({"jdbc", "postgres"})
public class JdbcUserServiceTest extends AbstractUserServiceTest {
}