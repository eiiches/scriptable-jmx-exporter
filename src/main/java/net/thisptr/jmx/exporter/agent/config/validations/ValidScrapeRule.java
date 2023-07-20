package net.thisptr.jmx.exporter.agent.config.validations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import net.thisptr.jmx.exporter.agent.config.validators.ScrapeRuleValidator;

@Constraint(validatedBy = ScrapeRuleValidator.class)
@Target({ ElementType.TYPE, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidScrapeRule {
	String message() default "ScrapeRule is invalid";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
