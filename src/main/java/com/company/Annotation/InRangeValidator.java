package com.company.Annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InRangeValidator implements ConstraintValidator<InRangeCustomAnnotation, Integer> {

	private int min;
	private int max;
	private String message;
	
	@Override
	public void initialize(InRangeCustomAnnotation customAnnotation) {
		this.message = customAnnotation.message();
		this.min =customAnnotation.min(); 
		this.max =customAnnotation.max();	
	}
	
	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		return value == null || (value >= min && value <= max); 
	}
	
	
}
