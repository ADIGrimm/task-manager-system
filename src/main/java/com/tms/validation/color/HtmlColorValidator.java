package com.tms.validation.color;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class HtmlColorValidator implements ConstraintValidator<HtmlColor, String> {

    private static final Pattern HTML_COLOR_PATTERN =
            Pattern.compile("^#(?:[0-9a-fA-F]{3}){1,2}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && HTML_COLOR_PATTERN.matcher(value).matches();
    }
}

