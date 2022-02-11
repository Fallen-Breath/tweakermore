package me.fallenbreath.tweakermore.util.dependency;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Strategy
{
	/**
	 * Enable only when all given conditions are satisfied
	 */
	Condition[] enableWhen() default {};

	/**
	 * Disable if any given condition is satisfied
	 * Overwrites field enableWhen
	 */
	Condition[] disableWhen() default {};
}
