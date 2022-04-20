package me.fallenbreath.tweakermore.config;

import fi.dy.masa.malilib.interfaces.IStringValue;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config
{
	Type type();

	Category category();

	/**
	 * Any of these restrictions satisfied => enable
	 */
	Restriction[] restriction() default {};

	boolean debug() default false;

	boolean devOnly() default false;

	enum Type implements IStringValue
	{
		GENERIC, HOTKEY, LIST, TWEAK, DISABLE, FIX;

		@Override
		public String getStringValue()
		{
			return StringUtils.translate("tweakermore.gui.config_type." + this.name().toLowerCase());
		}
	}

	enum Category
	{
		ALL, FEATURES, MC_TWEAKS, MOD_TWEAKS, PORTING, SETTING;

		public String getDisplayName()
		{
			return StringUtils.translate(String.format("tweakermore.gui.config_category.%s", this.name().toLowerCase()));
		}

		public String getDescription()
		{
			return StringUtils.translate(String.format("tweakermore.gui.config_category.%s.description", this.name().toLowerCase()));
		}
	}
}
