package com.example.domain;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.validation.ValidationUtils;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonPOJOBuilder(withPrefix = "")
public abstract class GenericBuilder<T> {
  protected abstract T nonValidatedBuild();

  public T build() {
    return validatedBuild();
  }

  public T validatedBuild() {
    T ret = nonValidatedBuild();
    Validator validator = null;
    if (validator != null) {
      Set<ConstraintViolation<T>> problems = validator.validate(ret);
      if (problems != null && !problems.isEmpty()) {
        throw new ConstraintViolationException("Could not build", problems);
      }
    }
    return ret;
  }
}