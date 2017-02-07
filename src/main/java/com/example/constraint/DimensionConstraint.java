package com.example.constraint;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@Target({ ANNOTATION_TYPE, FIELD, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@Min(1)
@Max(2000)
@Documented
public @interface DimensionConstraint {
  String message() default "Invalid Dimension";
  boolean optional() default false;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
