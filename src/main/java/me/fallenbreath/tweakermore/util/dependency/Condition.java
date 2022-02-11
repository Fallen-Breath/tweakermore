package me.fallenbreath.tweakermore.util.dependency;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Condition
{
	Type type() default Type.MOD;

	/**
	 * A mod dependency if type == MOD, e.g. "lithium" or "carpet"
	 * A full qualified mixin class name if type == MIXIN
	 */
	String value();

	/**
	 * All possible version range requirements, any of which getting matched results in the condition being true
	 * Used when type == MOD
	 * The condition is satisfied only when all the version matches all predicates or no predicate is given
	 * e.g. ">=1.2.3"
	 */
	String[] versionPredicates() default {};

	enum Type
	{
		MOD,
		MIXIN
	}
}
