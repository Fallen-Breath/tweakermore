package me.fallenbreath.tweakermore.util.mixin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ModRequire
{
	Requirement[] enableWhen() default {};
	Requirement[] disableWhen() default {};
}
