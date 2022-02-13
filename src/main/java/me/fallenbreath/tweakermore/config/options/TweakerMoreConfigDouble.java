package me.fallenbreath.tweakermore.config.options;

import fi.dy.masa.malilib.config.options.ConfigDouble;

public class TweakerMoreConfigDouble extends ConfigDouble implements TweakerMoreIConfigBase
{
	public TweakerMoreConfigDouble(String name, double defaultValue)
	{
		super(name, defaultValue, TWEAKERMORE_NAMESPACE_PREFIX + name + COMMENT_SUFFIX);
	}

	public TweakerMoreConfigDouble(String name, double defaultValue, double minValue, double maxValue)
	{
		super(name, defaultValue, minValue, maxValue, TWEAKERMORE_NAMESPACE_PREFIX + name + COMMENT_SUFFIX);
	}
}
