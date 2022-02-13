package me.fallenbreath.tweakermore.util.dependency;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Restriction
{
	/**
	 * Enable only when all given conditions are satisfied, like the "depends" entry in "fabric.mod.json"
	 */
	Condition[] enableWhen() default {};

	/**
	 * Disable if any given condition is satisfied, like the "conflicts" entry in "fabric.mod.json"
	 * Overwrites field enableWhen
	 */
	Condition[] disableWhen() default {};
}
