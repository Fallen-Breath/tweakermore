package me.fallenbreath.tweakermore.config.options;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.ConfigStringList;

public class TweakerMoreConfigStringList extends ConfigStringList implements TweakerMoreIConfigBase
{
	public TweakerMoreConfigStringList(String name, ImmutableList<String> defaultValue)
	{
		super(name, defaultValue, TWEAKERMORE_NAMESPACE_PREFIX + name + COMMENT_SUFFIX);
	}
}
