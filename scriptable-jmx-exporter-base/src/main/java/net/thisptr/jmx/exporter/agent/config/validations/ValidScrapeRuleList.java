package net.thisptr.jmx.exporter.agent.config.validations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import net.thisptr.jmx.exporter.agent.config.validators.ScrapeRuleListValidator;

@Constraint(validatedBy = ScrapeRuleListValidator.class)
@Target({ ElementType.TYPE, ElementType.TYPE_USE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidScrapeRuleList {
	String message() default "List<ScrapeRule> is invalid";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
