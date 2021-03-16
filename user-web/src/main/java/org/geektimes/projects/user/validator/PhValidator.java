package org.geektimes.projects.user.validator;


import org.geektimes.projects.user.validator.register.annotation.PhoneValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhValidator implements ConstraintValidator<PhoneValidator,String> {
    private String msg;

    private static final String REGEX="^1[0-9]{10}$";

    @Override
    public void initialize(PhoneValidator constraintAnnotation) {
        this.msg=constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Pattern.matches(REGEX,value);
    }
}
