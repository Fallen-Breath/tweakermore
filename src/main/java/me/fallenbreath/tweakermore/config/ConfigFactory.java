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

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import me.fallenbreath.tweakermore.config.options.*;

public abstract class ConfigFactory
{
	public static TweakerMoreConfigHotkey newConfigHotKey(String name, String defaultStorageString)
	{
		return new TweakerMoreConfigHotkey(name, defaultStorageString);
	}

	public static TweakerMoreConfigHotkey newConfigHotKey(String name, String defaultStorageString, KeybindSettings settings)
	{
		return new TweakerMoreConfigHotkey(name, defaultStorageString, settings);
	}

	public static TweakerMoreConfigHotkeyWithSwitch newConfigHotKeyWithSwitch(String name, boolean defaultEnableState, String defaultStorageString)
	{
		return new TweakerMoreConfigHotkeyWithSwitch(name, defaultEnableState, defaultStorageString);
	}

	public static TweakerMoreConfigHotkeyWithSwitch newConfigHotKeyWithSwitch(String name, boolean defaultEnableState, String defaultStorageString, KeybindSettings settings)
	{
		return new TweakerMoreConfigHotkeyWithSwitch(name, defaultEnableState, defaultStorageString, settings);
	}

	public static TweakerMoreConfigBooleanHotkeyed newConfigBooleanHotkeyed(String name)
	{
		return newConfigBooleanHotkeyed(name, false, "");
	}

	public static TweakerMoreConfigBooleanHotkeyed newConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultStorageString)
	{
		return new TweakerMoreConfigBooleanHotkeyed(name, defaultValue, defaultStorageString);
	}

	public static TweakerMoreConfigBoolean newConfigBoolean(String name, boolean defaultValue)
	{
		return new TweakerMoreConfigBoolean(name, defaultValue);
	}

	public static TweakerMoreConfigInteger newConfigInteger(String name, int defaultValue)
	{
		return new TweakerMoreConfigInteger(name, defaultValue);
	}

	public static TweakerMoreConfigInteger newConfigInteger(String name, int defaultValue, int minValue, int maxValue)
	{
		return new TweakerMoreConfigInteger(name, defaultValue, minValue, maxValue);
	}

	public static TweakerMoreConfigDouble newConfigDouble(String name, double defaultValue)
	{
		return new TweakerMoreConfigDouble(name, defaultValue);
	}

	public static TweakerMoreConfigDouble newConfigDouble(String name, double defaultValue, double minValue, double maxValue)
	{
		return new TweakerMoreConfigDouble(name, defaultValue, minValue, maxValue);
	}

	public static TweakerMoreConfigString newConfigString(String name, String defaultValue)
	{
		return new TweakerMoreConfigString(name, defaultValue);
	}

	public static TweakerMoreConfigStringList newConfigStringList(String name, ImmutableList<String> defaultValue)
	{
		return new TweakerMoreConfigStringList(name, defaultValue);
	}

	public static TweakerMoreConfigOptionList newConfigOptionList(String name, IConfigOptionListEntry defaultValue)
	{
		return new TweakerMoreConfigOptionList(name, defaultValue);
	}

	public static TweakerMoreConfigOptionListHotkeyed newConfigOptionListHotkeyed(String name, IConfigOptionListEntry defaultValue)
	{
		return newConfigOptionListHotkeyed(name, defaultValue, "");
	}

	public static TweakerMoreConfigOptionListHotkeyed newConfigOptionListHotkeyed(String name, IConfigOptionListEntry defaultValue, String defaultStorageString)
	{
		return new TweakerMoreConfigOptionListHotkeyed(name, defaultValue, defaultStorageString);
	}

	public static TweakerMoreConfigOptionListHotkeyed newConfigOptionListHotkeyed(String name, IConfigOptionListEntry defaultValue, String defaultStorageString, KeybindSettings settings)
	{
		return new TweakerMoreConfigOptionListHotkeyed(name, defaultValue, defaultStorageString, settings);
	}
}
