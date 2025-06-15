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
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import me.fallenbreath.conditionalmixin.api.util.VersionChecker;
import me.fallenbreath.tweakermore.util.ModIds;

public class TweakerMoreConfigBooleanHotkeyed extends ConfigBooleanHotkeyed implements TweakerMoreIConfigBase
{
	/**
	 * see also: {@link me.fallenbreath.tweakermore.mixins.core.migration.ConfigBooleanHotkeyedMixin}
	 */
	private final boolean OLD_CONFIG_BOOLEAN_HOTKEYED_STORAGE_FORMAT = VersionChecker.doesModVersionSatisfyPredicate(ModIds.malilib, "<0.11.5");

	public TweakerMoreConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey)
	{
		super(name, defaultValue, defaultHotkey, TWEAKERMORE_NAMESPACE_PREFIX + name + COMMENT_SUFFIX, TWEAKERMORE_NAMESPACE_PREFIX + name + PRETTY_NAME_SUFFIX);
	}

	@Override
	public void setValueFromJsonElement(JsonElement element)
	{
		boolean oldValue = this.getBooleanValue();

		if (OLD_CONFIG_BOOLEAN_HOTKEYED_STORAGE_FORMAT && element.isJsonObject() && element.getAsJsonObject().has("enabled"))
		{
			element = element.getAsJsonObject().get("enabled");
		}
		super.setValueFromJsonElement(element);

		if (oldValue != this.getBooleanValue())
		{
			this.onValueChanged(true);
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
