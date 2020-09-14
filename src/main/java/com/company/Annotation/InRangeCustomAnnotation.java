package com.company.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { InRangeValidator.class})
public @interface InRangeCustomAnnotation {
	String message() default "prix doit être entre 0 et 100";
	int min() default Integer.MIN_VALUE;
	int max() default Integer.MAX_VALUE;

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
  
}
