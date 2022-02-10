package me.fallenbreath.tweakermore.util.mixin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ModRequire
{
	/**
	 * Enable the mixin only when all given conditions are satisfied
	 */
	Condition[] enableWhen() default {};

	/**
	 * Disable the mixin if any given condition is satisfied
	 * Overwrites field enableWhen
	 */
	Condition[] disableWhen() default {};
}
