package org.tiwpr.szymie.models.validators;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Documented
@Constraint(validatedBy = DatePattern.DatePatternValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DatePattern {

    String message() default "{org.tiwpr.szymie.models.validators.DatePattern}";
    String regexp();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class DatePatternValidator implements ConstraintValidator<DatePattern, String> {

        private String format;

        @Override
        public void initialize(final DatePattern datePattern) {
            format = datePattern.regexp();
        }

        @Override
        public boolean isValid(final String date, final ConstraintValidatorContext constraintValidatorContext) {
            return date == null || isDateValid(date);
        }

        private boolean isDateValid(final String date) {

            DateFormat dateFormat = new SimpleDateFormat(format);

            try {
                dateFormat.parse(date);
                return true;
            } catch (ParseException exception) {
                return false;
            }
        }
    }
}
