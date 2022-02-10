package me.fallenbreath.tweakermore.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.*;
import me.fallenbreath.tweakermore.util.StringUtil;

public abstract class ConfigFactory
{
	private static final String PREFIX = StringUtil.TWEAKERMORE_NAMESPACE_PREFIX;
	private static final String COMMENT_SUFFIX = ".comment";
	private static final String PRETTY_NAME_SUFFIX = ".pretty_name";

	public static ConfigHotkey newConfigHotKey(String name, String defaultHotkey)
	{
		return new ConfigHotkey(PREFIX + name, defaultHotkey, PREFIX + name + COMMENT_SUFFIX);
	}

	public static ConfigBooleanHotkeyed newConfigBooleanHotkeyed(String name)
	{
		return newConfigBooleanHotkeyed(name, false, "");
	}

	public static ConfigBooleanHotkeyed newConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotKey)
	{
		return new ConfigBooleanHotkeyed(PREFIX + name, defaultValue, defaultHotKey, PREFIX + name + COMMENT_SUFFIX, PREFIX + name + PRETTY_NAME_SUFFIX);
	}

	public static ConfigBoolean newConfigBoolean(String name, boolean defaultValue)
	{
		return new ConfigBoolean(PREFIX + name, defaultValue, PREFIX + name + COMMENT_SUFFIX);
	}

	public static ConfigInteger newConfigInteger(String name, int defaultValue, int minValue, int maxValue)
	{
		return new ConfigInteger(PREFIX + name, defaultValue, minValue, maxValue, PREFIX + name + COMMENT_SUFFIX);
	}

	public static ConfigDouble newConfigDouble(String name, double defaultValue, double minValue, double maxValue)
	{
		return new ConfigDouble(PREFIX + name, defaultValue, minValue, maxValue, PREFIX + name + COMMENT_SUFFIX);
	}

	public static ConfigString newConfigString(String name, String defaultValue)
	{
		return new ConfigString(PREFIX + name, defaultValue, PREFIX + name + COMMENT_SUFFIX);
	}

	public static ConfigStringList newConfigStringList(String name, ImmutableList<String> defaultValue)
	{
		return new ConfigStringList(PREFIX + name, defaultValue, PREFIX + name + COMMENT_SUFFIX);
	}

	public static ConfigOptionList newConfigOptionList(String name, IConfigOptionListEntry defaultValue)
	{
		return new ConfigOptionList(PREFIX + name, defaultValue, PREFIX + name + COMMENT_SUFFIX);
	}
}
