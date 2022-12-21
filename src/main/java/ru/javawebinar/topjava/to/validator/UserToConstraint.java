package ru.javawebinar.topjava.to.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserToValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface UserToConstraint {
    String message() default "Bad UserTo";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
