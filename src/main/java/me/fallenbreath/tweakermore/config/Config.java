package me.fallenbreath.tweakermore.config;

import fi.dy.masa.malilib.interfaces.IStringValue;
import fi.dy.masa.malilib.util.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config
{
	Type[] value();

	enum Type implements IStringValue
	{
		GENERIC, HOTKEY, LIST, TWEAK, DISABLE, CONFIG;

		@Override
		public String getStringValue()
		{
			return StringUtils.translate("tweakermore.gui.config_type." + this.name().toLowerCase());
		}
	}
}
