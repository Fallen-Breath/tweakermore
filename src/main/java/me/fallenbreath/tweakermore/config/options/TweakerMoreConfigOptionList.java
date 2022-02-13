package me.fallenbreath.tweakermore.config.options;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigOptionList;

public class TweakerMoreConfigOptionList extends ConfigOptionList implements TweakerMoreIConfigBase
{
	public TweakerMoreConfigOptionList(String name, IConfigOptionListEntry defaultValue)
	{
		super(name, defaultValue, TWEAKERMORE_NAMESPACE_PREFIX + name + COMMENT_SUFFIX);
	}
}
