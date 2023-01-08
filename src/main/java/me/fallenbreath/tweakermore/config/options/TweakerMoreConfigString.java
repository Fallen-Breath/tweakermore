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

package me.fallenbreath.tweakermore.config.options;

import com.google.gson.JsonElement;
import fi.dy.masa.malilib.config.options.ConfigString;

import java.util.Objects;

public class TweakerMoreConfigString extends ConfigString implements TweakerMoreIConfigBase
{
	public TweakerMoreConfigString(String name, String defaultValue)
	{
		super(name, defaultValue, TWEAKERMORE_NAMESPACE_PREFIX + name + COMMENT_SUFFIX);
	}

	@Override
	public void setValueFromJsonElement(JsonElement element)
	{
		String oldValue = this.getStringValue();

		super.setValueFromJsonElement(element);

		if (!Objects.equals(oldValue, this.getStringValue()))
		{
			this.onValueChanged();
		}
	}

	@Override
	public void onValueChanged()
	{
		this.onValueChanged(false);
	}

	@Override
	public void onValueChanged(boolean fromFile)
	{
		super.onValueChanged();
		if (!fromFile)
		{
			this.updateStatisticOnUse();
		}
	}
}
