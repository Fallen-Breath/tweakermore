package me.fallenbreath.tweakermore.config.options;

import com.google.gson.JsonElement;
import fi.dy.masa.malilib.config.options.ConfigInteger;

public class TweakerMoreConfigInteger extends ConfigInteger implements TweakerMoreIConfigBase
{
	public TweakerMoreConfigInteger(String name, int defaultValue)
	{
		super(name, defaultValue, TWEAKERMORE_NAMESPACE_PREFIX + name + COMMENT_SUFFIX);
	}

	public TweakerMoreConfigInteger(String name, int defaultValue, int minValue, int maxValue)
	{
		super(name, defaultValue, minValue, maxValue, TWEAKERMORE_NAMESPACE_PREFIX + name + COMMENT_SUFFIX);
	}

	@Override
	public void setValueFromJsonElement(JsonElement element)
	{
		int oldValue = this.getIntegerValue();

		super.setValueFromJsonElement(element);

		if (oldValue != this.getIntegerValue())
		{
			this.onValueChanged();
		}
	}
}
