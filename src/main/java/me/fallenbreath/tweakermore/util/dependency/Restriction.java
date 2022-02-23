package me.fallenbreath.tweakermore.util.dependency;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Restriction
{
	/**
	 * Enable only when all given conditions are satisfied, like the "depends" entry in "fabric.mod.json"
	 */
	Condition[] require() default {};

	/**
	 * Disable if any given condition is satisfied, like the "conflicts" entry in "fabric.mod.json"
	 * Has higher priority than field "require"
	 */
	Condition[] conflict() default {};
}
