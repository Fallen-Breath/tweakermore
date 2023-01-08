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

package me.fallenbreath.tweakermore.config.options.listentries;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

import java.util.Arrays;

public interface EnumOptionEntry extends IConfigOptionListEntry
{
	////////////////////////////////////////////
	//     enum classes should have these     //
	////////////////////////////////////////////

	String name();
	int ordinal();

	////////////////////////////////////////////
	//            to be implemented           //
	////////////////////////////////////////////

	/**
	 * Redirect this to Enum.values()
	 */
	EnumOptionEntry[] getAllValues();

	/**
	 * The default/fallback value
	 */
	EnumOptionEntry getDefault();

	/**
	 * The translation key prefix, e.g. "tweakermore.value.my_value_type."
	 */
	String getTranslationPrefix();

	////////////////////////////////////////////////////
	//   implementations for IConfigOptionListEntry   //
	////////////////////////////////////////////////////

	@Override
	default String getStringValue()
	{
		return this.name().toLowerCase();
	}

	@Override
	default String getDisplayName()
	{
		return StringUtils.translate(this.getTranslationPrefix() + this.name().toLowerCase());
	}

	@Override
	default IConfigOptionListEntry cycle(boolean forward)
	{
		int index = this.ordinal();
		EnumOptionEntry[] values = this.getAllValues();

		index += forward ? 1 : -1;
		index = (index + values.length) % values.length;

		return values[index];
	}

	@Override
	default IConfigOptionListEntry fromString(String value)
	{
		return Arrays.stream(getAllValues()).
				filter(o -> o.name().equalsIgnoreCase(value)).
				findFirst().
				orElseGet(this::getDefault);
	}
}
