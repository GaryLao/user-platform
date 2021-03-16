package org.geektimes.projects.user.validator.register.annotation;

import org.geektimes.projects.user.validator.PSValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {PSValidator.class})
public @interface PasswordValidator {
    String message() default "密码格式错 密码6-18";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
