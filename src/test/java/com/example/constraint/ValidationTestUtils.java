package com.example.constraint;
import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.hibernate.validator.HibernateValidator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class ValidationTestUtils {

	// Not Thread safe
	private static LocalValidatorFactoryBean v = null;

	public static Validator getValidator() {
		if (v == null) {
			v = new LocalValidatorFactoryBean();
			v.setProviderClass(HibernateValidator.class);
			v.afterPropertiesSet();
		}
		return v;
	}

	  public static void assertNumberOfViolations(Set<? extends ConstraintViolation<?>> violations,
		      int numberOfViolations) {
		    assertEquals("Wrong number of constraint violations", numberOfViolations, violations.size());
		  }

	public static void assertNumberOfViolationsOnObject(Object o, int numberOfViolations) {
		Validator v = getValidator();
		assertNumberOfViolations(v.validate(o), numberOfViolations);
	}

}
