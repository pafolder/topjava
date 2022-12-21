package ru.javawebinar.topjava.to.validator;

import org.springframework.stereotype.Component;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
        User user;
        try {
            user = userService.getByEmail(email);
        } catch (NotFoundException e) {
            return true;
        }
        constraintValidatorContext.buildConstraintViolationWithTemplate(
                        "User " + user.getName() + " already has this email")
                .addConstraintViolation();
        constraintValidatorContext.disableDefaultConstraintViolation();
        return false;
    }
}
