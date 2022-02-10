package me.fallenbreath.tweakermore.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.*;

public abstract class ConfigFactory
{
	public static ConfigHotkey newConfigHotKey(String name, String defaultHotkey)
	{
		return new ConfigHotkey(name, defaultHotkey, name + ".comment");
	}

	public static ConfigBooleanHotkeyed newConfigBooleanHotkeyed(String name)
	{
		return newConfigBooleanHotkeyed(name, false, "");
	}

	public static ConfigBooleanHotkeyed newConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotKey)
	{
		return new ConfigBooleanHotkeyed(name, defaultValue, defaultHotKey, name + ".comment", name + ".pretty_name");
	}

	public static ConfigBoolean newConfigBoolean(String name, boolean defaultValue)
	{
		return new ConfigBoolean(name, defaultValue, name + ".comment");
	}

	public static ConfigInteger newConfigInteger(String name, int defaultValue, int minValue, int maxValue)
	{
		return new ConfigInteger(name, defaultValue, minValue, maxValue, name + ".comment");
	}

	public static ConfigDouble newConfigDouble(String name, double defaultValue, double minValue, double maxValue)
	{
		return new ConfigDouble(name, defaultValue, minValue, maxValue, name + ".comment");
	}

	public static ConfigString newConfigString(String name, String defaultValue)
	{
		return new ConfigString(name, defaultValue, name + ".comment");
	}

	public static ConfigStringList newConfigStringList(String name, ImmutableList<String> defaultValue)
	{
		return new ConfigStringList(name, defaultValue, name + ".comment");
	}

	public static ConfigOptionList newConfigOptionList(String name, IConfigOptionListEntry defaultValue)
	{
		return new ConfigOptionList(name, defaultValue, name + ".comment");
	}
}
