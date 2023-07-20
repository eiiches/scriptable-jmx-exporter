package net.thisptr.jmx.exporter.agent.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

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
