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

import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import org.jetbrains.annotations.Nullable;

public class TweakerMoreConfigHotkey extends ConfigHotkey implements TweakerMoreIConfigBase
{
	public TweakerMoreConfigHotkey(String name, String defaultStorageString)
	{
		super(name, defaultStorageString, TWEAKERMORE_NAMESPACE_PREFIX + name + COMMENT_SUFFIX);
	}

	public TweakerMoreConfigHotkey(String name, String defaultStorageString, KeybindSettings settings)
	{
		super(name, defaultStorageString, settings, TWEAKERMORE_NAMESPACE_PREFIX + name + COMMENT_SUFFIX);
	}

	/**
	 * Use this instead of {@code getKeybind().setCallback} directly
	 * So the config statistic can be updated correctly
	 */
	public void setCallBack(@Nullable IHotkeyCallback callback)
	{
		// don't count OPEN_TWEAKERMORE_CONFIG_GUI cuz technically it's just a shortcut option instead of a feature option
		if (callback == null || this == TweakerMoreConfigs.OPEN_TWEAKERMORE_CONFIG_GUI)
		{
			this.getKeybind().setCallback(callback);
		}
		else
		{
			this.getKeybind().setCallback((action, key) -> {
				boolean ret = callback.onKeyAction(action, key);
				this.updateStatisticOnUse();
				return ret;
			});
		}
	}

	public boolean isKeybindHeld()
	{
		return this.getKeybind().isKeybindHeld();
	}

	@Override
	public void onValueChanged()
	{
		this.onValueChanged(true);
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
