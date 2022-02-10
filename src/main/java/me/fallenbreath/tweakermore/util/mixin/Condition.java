package me.fallenbreath.tweakermore.util.mixin;

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

	enum Type
	{
		MOD,
		MIXIN
	}
}
