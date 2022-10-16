package ru.javawebinar.topjava;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.web.user.AdminRestController;

import java.util.Arrays;

public class SpringMain {
    private static final Logger log = LoggerFactory.getLogger(SpringMain.class);

    public static void main(String[] args) {
        // java 7 automatic resource management (ARM)
        try (ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml")) {
            System.out.println("Bean definition names: " + Arrays.toString(appCtx.getBeanDefinitionNames()));
            AdminRestController adminUserController = appCtx.getBean(AdminRestController.class);
            adminUserController.create(new User(null, "Petrov", "Petrov@mail.ru", "password", Role.ADMIN));
            adminUserController.create(new User(null, "PetrovA", "Petrova@mail.ru", "password", Role.ADMIN));
            adminUserController.create(new User(null, "PetrovA", "Petrova@mail.ru", "password", Role.ADMIN));
            adminUserController.create(new User(null, "PetrovA", "petrova@mail.ru", "password", Role.ADMIN));
            adminUserController.create(new User(null, "userName", "email@mail.ru", "password", Role.ADMIN));
            adminUserController.create(new User(null, "Ivanov", "Ivanof@mail.ru", "password", Role.ADMIN));
            adminUserController.create(new User(null, "Ivanov", "ivanov@mail.ru", "password", Role.ADMIN));
            adminUserController.create(new User(null, "Ivanova", "ivanova@mail.ru", "password", Role.ADMIN));
            adminUserController.create(new User(null, "AIvanov", "AIvanov@mail.ru", "password", Role.ADMIN));
            for (User user : adminUserController.getAll()) {
                log.debug(user.getName() + " " + user.getEmail());
            }
        }
    }
}
