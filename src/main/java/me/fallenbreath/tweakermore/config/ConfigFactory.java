package me.fallenbreath.tweakermore.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import me.fallenbreath.tweakermore.config.options.*;

public abstract class ConfigFactory
{
	public static ConfigHotkey newConfigHotKey(String name, String defaultStorageString)
	{
		return new TweakerMoreConfigHotkey(name, defaultStorageString);
	}

	public static ConfigHotkey newConfigHotKey(String name, String defaultStorageString, KeybindSettings settings)
	{
		return new TweakerMoreConfigHotkey(name, defaultStorageString, settings);
	}

	public static ConfigBooleanHotkeyed newConfigBooleanHotkeyed(String name)
	{
		return newConfigBooleanHotkeyed(name, false, "");
	}

	public static ConfigBooleanHotkeyed newConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultStorageString)
	{
		return new TweakerMoreConfigBooleanHotkeyed(name, defaultValue, defaultStorageString);
	}

	public static ConfigBoolean newConfigBoolean(String name, boolean defaultValue)
	{
		return new TweakerMoreConfigBoolean(name, defaultValue);
	}

	public static ConfigInteger newConfigInteger(String name, int defaultValue)
	{
		return new TweakerMoreConfigInteger(name, defaultValue);
	}

	public static ConfigInteger newConfigInteger(String name, int defaultValue, int minValue, int maxValue)
	{
		return new TweakerMoreConfigInteger(name, defaultValue, minValue, maxValue);
	}

	public static ConfigDouble newConfigDouble(String name, double defaultValue)
	{
		return new TweakerMoreConfigDouble(name, defaultValue);
	}

	public static ConfigDouble newConfigDouble(String name, double defaultValue, double minValue, double maxValue)
	{
		return new TweakerMoreConfigDouble(name, defaultValue, minValue, maxValue);
	}

	public static ConfigString newConfigString(String name, String defaultValue)
	{
		return new TweakerMoreConfigString(name, defaultValue);
	}

	public static ConfigStringList newConfigStringList(String name, ImmutableList<String> defaultValue)
	{
		return new TweakerMoreConfigStringList(name, defaultValue);
	}

	public static ConfigOptionList newConfigOptionList(String name, IConfigOptionListEntry defaultValue)
	{
		return new TweakerMoreConfigOptionList(name, defaultValue);
	}
}
