package me.fallenbreath.tweakermore.config.options;

import com.google.gson.JsonElement;
import fi.dy.masa.malilib.config.options.ConfigBoolean;

public class TweakerMoreConfigBoolean extends ConfigBoolean implements TweakerMoreIConfigBase
{
	public TweakerMoreConfigBoolean(String name, boolean defaultValue)
	{
		super(name, defaultValue, TWEAKERMORE_NAMESPACE_PREFIX + name + COMMENT_SUFFIX);
	}

	@Override
	public void setValueFromJsonElement(JsonElement element)
	{
		boolean oldValue = this.getBooleanValue();

		super.setValueFromJsonElement(element);

		if (oldValue != this.getBooleanValue())
		{
			this.onValueChanged();
		}
	}
}
