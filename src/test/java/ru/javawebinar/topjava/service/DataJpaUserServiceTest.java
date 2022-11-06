package ru.javawebinar.topjava.service;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.basic.AbstractUserServiceTest;

@ActiveProfiles({"datajpa", "postgres"})
public class DataJpaUserServiceTest extends AbstractUserServiceTest {
}
