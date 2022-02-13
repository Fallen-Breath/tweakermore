package me.fallenbreath.tweakermore.config.options;

import fi.dy.masa.malilib.config.options.ConfigString;

public class TweakerMoreConfigString extends ConfigString implements TweakerMoreIConfigBase
{
	public TweakerMoreConfigString(String name, String defaultValue)
	{
		super(name, defaultValue, TWEAKERMORE_NAMESPACE_PREFIX + name + COMMENT_SUFFIX);
	}
}
