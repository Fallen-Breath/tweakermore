package me.fallenbreath.tweakermore.config;

import fi.dy.masa.malilib.config.IConfigBase;
import me.fallenbreath.tweakermore.util.FabricUtil;

import java.util.Arrays;

public class TweakerMoreOption
{
	private final Config annotation;
	private final IConfigBase option;
	private final boolean enable;

	public TweakerMoreOption(Config annotation, IConfigBase option)
	{
		this.annotation = annotation;
		this.option = option;
		this.enable = Arrays.stream(getRequiredModIds()).allMatch(FabricUtil::isModLoaded);
	}

	public Config.Type getType()
	{
		return this.annotation.value();
	}

	public Config.Category getCategory()
	{
		return this.annotation.category();
	}

	public String[] getRequiredModIds()
	{
		return this.annotation.modRequire();
	}

	public boolean isEnabled()
	{
		return this.enable;
	}

	public IConfigBase getOption()
	{
		return this.option;
	}
}
