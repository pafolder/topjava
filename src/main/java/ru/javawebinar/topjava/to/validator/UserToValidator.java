package ru.javawebinar.topjava.to.validator;

import org.springframework.stereotype.Component;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

@Component
public class UserToValidator implements ConstraintValidator<UserToConstraint, UserTo> {
    UserToConstraint constraintAnnotation;
    UserService userService;

    public UserToValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(UserToConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(UserTo userTo, ConstraintValidatorContext constraintValidatorContext) {
        AuthorizedUser authorizedUser = SecurityUtil.safeGet();
        if (userTo.getId() == null && authorizedUser != null) {
            userTo.setId(authorizedUser.getId());
        }
        User user;
        try {
            user = userService.getByEmail(userTo.getEmail());
        } catch (NotFoundException e) {
            return true;
        }
        if (Objects.equals(userTo.getId(), user.getId())) {
            return true;
        }
        constraintValidatorContext.buildConstraintViolationWithTemplate(
                        "User " + user.getName() + " already has this email")
                .addConstraintViolation();
        constraintValidatorContext.disableDefaultConstraintViolation();
        return false;
    }

}
