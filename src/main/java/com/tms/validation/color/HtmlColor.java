package com.tms.validation.color;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HtmlColorValidator.class)
public @interface HtmlColor {
    String message() default "Invalid HTML color format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

