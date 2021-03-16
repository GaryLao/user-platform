package org.geektimes.projects.user.validator.register.annotation;

import org.geektimes.projects.user.validator.PhValidator;

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
@Constraint(validatedBy = {PhValidator.class})
public @interface PhoneValidator {
    String message() default "手机格式错误";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
