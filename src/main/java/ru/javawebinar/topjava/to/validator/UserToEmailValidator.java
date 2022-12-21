package ru.javawebinar.topjava.to.validator;

import org.springframework.stereotype.Component;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

@Component
public class UserToEmailValidator implements ConstraintValidator<UserToEmailConstraint, String> {
    UserToEmailConstraint constraintAnnotation;
    UserService userService;

    public UserToEmailValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(UserToEmailConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        AuthorizedUser authorizedUser = SecurityUtil.safeGet();
        Integer id = null;
        if (authorizedUser != null) {
            id = authorizedUser.getId();
        }
        User user;
        try {
            user = userService.getByEmail(email);
        } catch (NotFoundException e) {
            return true;
        }
        if (Objects.equals(id, user.getId())) {
            return true;
        }
        constraintValidatorContext.buildConstraintViolationWithTemplate(
                        "User " + user.getName() + " has this email")
                .addConstraintViolation();
        constraintValidatorContext.disableDefaultConstraintViolation();
        return false;
    }
}
