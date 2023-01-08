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

public enum WeatherOverrideValue implements EnumOptionEntry
{
	CLEAR(false, false),
	RAIN(true, false),
	THUNDER(true, true);

	public static final WeatherOverrideValue DEFAULT = CLEAR;

	private final boolean raining;
	private final boolean thundering;

	WeatherOverrideValue(boolean raining, boolean thundering)
	{
		this.raining = raining;
		this.thundering = thundering;
	}

	public float getRainGradient()
	{
		return this.raining ? 1.0F : 0.0F;
	}

	public float getThunderGradient()
	{
		return this.thundering ? 1.0F : 0.0F;
	}

	@Override
	public WeatherOverrideValue[] getAllValues()
	{
		return values();
	}

	@Override
	public WeatherOverrideValue getDefault()
	{
		return DEFAULT;
	}

	@Override
	public String getTranslationPrefix()
	{
		return "tweakermore.list_entry.weatherOverrideValue.";
	}
}
