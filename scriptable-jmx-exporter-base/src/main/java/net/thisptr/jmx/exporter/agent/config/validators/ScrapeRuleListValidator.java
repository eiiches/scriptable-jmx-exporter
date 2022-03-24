package net.thisptr.jmx.exporter.agent.config.validators;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.thisptr.jmx.exporter.agent.config.Config.ScrapeRule;
import net.thisptr.jmx.exporter.agent.config.validations.ValidScrapeRuleList;

public class ScrapeRuleListValidator implements ConstraintValidator<ValidScrapeRuleList, List<ScrapeRule>> {

	@Override
	public boolean isValid(final List<ScrapeRule> value, final ConstraintValidatorContext context) {
		if (value == null)
			return true;

		boolean valid = true;

		boolean allHandled = false;
		for (int i = 0; i < value.size(); i++) {
			final ScrapeRule rule = value.get(i);
			if (allHandled) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate("The rule is not reachable. No rules are allowed after a match-all rule.")
						.addPropertyNode("[" + i + "]").addConstraintViolation();
				valid = false;
			} else {
				if ((rule.patterns == null || rule.patterns.isEmpty()) && rule.condition == null) {
					allHandled = true;
				}
			}
		}

		return valid;
	}
}
