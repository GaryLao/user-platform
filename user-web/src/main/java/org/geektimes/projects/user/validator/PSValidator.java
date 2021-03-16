package org.geektimes.projects.user.validator;

import org.geektimes.projects.user.validator.register.annotation.PasswordValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PSValidator implements ConstraintValidator<PasswordValidator,String> {
    private String msg;

    private static final String REGEX="^.{6,18}$";
    @Override
    public void initialize(PasswordValidator constraintAnnotation) {
        this.msg=constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Pattern.matches(REGEX, value);
    }
}
