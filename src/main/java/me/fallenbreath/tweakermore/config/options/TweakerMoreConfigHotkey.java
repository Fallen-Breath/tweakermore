package me.fallenbreath.tweakermore.config.options;

import fi.dy.masa.malilib.config.options.ConfigHotkey;

public class TweakerMoreConfigHotkey extends ConfigHotkey implements TweakerMoreIConfigBase
{
	public TweakerMoreConfigHotkey(String name, String defaultStorageString)
	{
		super(name, defaultStorageString, TWEAKERMORE_NAMESPACE_PREFIX + name + COMMENT_SUFFIX);
	}
}
