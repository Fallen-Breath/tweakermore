package me.fallenbreath.tweakermore.config.options;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import fi.dy.masa.malilib.config.options.ConfigStringList;

import java.util.List;

public class TweakerMoreConfigStringList extends ConfigStringList implements TweakerMoreIConfigBase
{
	public TweakerMoreConfigStringList(String name, ImmutableList<String> defaultValue)
	{
		super(name, defaultValue, TWEAKERMORE_NAMESPACE_PREFIX + name + COMMENT_SUFFIX);
	}

	@Override
	public void setValueFromJsonElement(JsonElement element)
	{
		List<String> oldValue = Lists.newArrayList(this.getStrings());

		super.setValueFromJsonElement(element);

		if (!oldValue.equals(this.getStrings()))
		{
			this.onValueChanged();
		}
	}
}
