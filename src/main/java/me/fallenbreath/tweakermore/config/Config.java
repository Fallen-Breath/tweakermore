package me.fallenbreath.tweakermore.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config
{
	Type[] value();

	enum Type
	{
		GENERIC, HOTKEY, LIST, TWEAK, DISABLE, CONFIG
	}
}
