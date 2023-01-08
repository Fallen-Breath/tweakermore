/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

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
