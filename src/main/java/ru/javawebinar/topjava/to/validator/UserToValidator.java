package ru.javawebinar.topjava.to.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.Objects;

@Component
public class UserToValidator implements Validator {
    UserService userService;

    public UserToValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        if ( clazz == UserTo.class ) {
            return true;
        }
        return false;
    }

    @Override
    public void validate(Object object, Errors errors) {
        UserTo userTo = (UserTo) object;
        AuthorizedUser authorizedUser = SecurityUtil.safeGet();
        if (userTo.getId() == null && authorizedUser != null) {
            userTo.setId(authorizedUser.getId());
        }
        User user;
        try {
            user = userService.getByEmail(userTo.getEmail());
        } catch (NotFoundException e) {
            return;
        }
        if (Objects.equals(userTo.getId(), user.getId())) {
            return;
        }
        String errorMessage = "User " + user.getName() + " already has " + userTo.getEmail() + " email";
        errors.rejectValue("email", "Validation error:", errorMessage);
    }
}
