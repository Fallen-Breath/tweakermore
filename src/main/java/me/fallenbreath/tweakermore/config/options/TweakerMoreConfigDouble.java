package me.fallenbreath.tweakermore.config.options;

import com.google.gson.JsonElement;
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

	@Override
	public void setValueFromJsonElement(JsonElement element)
	{
		double oldValue = this.getDoubleValue();

		super.setValueFromJsonElement(element);

		if (oldValue != this.getDoubleValue())
		{
			this.onValueChanged();
		}
	}
}
