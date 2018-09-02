package net.thisptr.java.prometheus.metrics.agent.utils;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class MoreValidators {
	public static <T> void validate(final T obj) {
		try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
			final Validator validator = validatorFactory.getValidator();
			final Set<ConstraintViolation<T>> violations = validator.validate(obj);
			if (!violations.isEmpty())
				throw new ConstraintViolationException(violations);
		}
	}
}
