package me.fallenbreath.tweakermore.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.*;

public abstract class ConfigFactory
{
	public static ConfigHotkey newConfigHotKey(String name, String defaultHotkey)
	{
		return new ConfigHotkey("tweakermore." + name, defaultHotkey, "tweakermore." + name + ".comment");
	}

	public static ConfigBooleanHotkeyed newConfigBooleanHotkeyed(String name)
	{
		return newConfigBooleanHotkeyed(name, false, "");
	}

	public static ConfigBooleanHotkeyed newConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotKey)
	{
		return new ConfigBooleanHotkeyed("tweakermore." + name, defaultValue, defaultHotKey, "tweakermore." + name + ".comment", "tweakermore." + name + ".pretty_name");
	}

	public static ConfigBoolean newConfigBoolean(String name, boolean defaultValue)
	{
		return new ConfigBoolean("tweakermore." + name, defaultValue, "tweakermore." + name + ".comment");
	}

	public static ConfigInteger newConfigInteger(String name, int defaultValue, int minValue, int maxValue)
	{
		return new ConfigInteger("tweakermore." + name, defaultValue, minValue, maxValue, "tweakermore." + name + ".comment");
	}

	public static ConfigDouble newConfigDouble(String name, double defaultValue, double minValue, double maxValue)
	{
		return new ConfigDouble("tweakermore." + name, defaultValue, minValue, maxValue, "tweakermore." + name + ".comment");
	}

	public static ConfigString newConfigString(String name, String defaultValue)
	{
		return new ConfigString("tweakermore." + name, defaultValue, "tweakermore." + name + ".comment");
	}

	public static ConfigStringList newConfigStringList(String name, ImmutableList<String> defaultValue)
	{
		return new ConfigStringList("tweakermore." + name, defaultValue, "tweakermore." + name + ".comment");
	}

	public static ConfigOptionList newConfigOptionList(String name, IConfigOptionListEntry defaultValue)
	{
		return new ConfigOptionList("tweakermore." + name, defaultValue, "tweakermore." + name + ".comment");
	}
}
