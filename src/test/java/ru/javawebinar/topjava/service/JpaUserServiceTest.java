package ru.javawebinar.topjava.service;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.basic.AbstractUserServiceTest;

@ActiveProfiles({"jpa", "postgres"})
public class JpaUserServiceTest extends AbstractUserServiceTest {
}
