package net.thisptr.jmx.exporter.agent.config.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.thisptr.jmx.exporter.agent.config.Config.ScrapeRule;
import net.thisptr.jmx.exporter.agent.config.validations.ValidScrapeRule;

public class ScrapeRuleValidator implements ConstraintValidator<ValidScrapeRule, ScrapeRule> {

	@Override
	public boolean isValid(final ScrapeRule value, final ConstraintValidatorContext context) {
		if (value == null)
			return true;

		if (value.skip && value.transform != null) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("The transform script must not be specified when skip is true because it will never be executed.")
					.addPropertyNode("transform").addConstraintViolation();
			return false;
		}

		if (!value.skip && value.transform == null) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("The transform script must be provided when skip is false.")
					.addPropertyNode("transform").addConstraintViolation();
			return false;
		}

		return true;
	}
}
