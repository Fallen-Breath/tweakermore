package me.fallenbreath.tweakermore.config.options;

import com.google.gson.JsonElement;
import fi.dy.masa.malilib.config.options.ConfigString;

import java.util.Objects;

public class TweakerMoreConfigString extends ConfigString implements TweakerMoreIConfigBase
{
	public TweakerMoreConfigString(String name, String defaultValue)
	{
		super(name, defaultValue, TWEAKERMORE_NAMESPACE_PREFIX + name + COMMENT_SUFFIX);
	}

	@Override
	public void setValueFromJsonElement(JsonElement element)
	{
		String oldValue = this.getStringValue();

		super.setValueFromJsonElement(element);

		if (!Objects.equals(oldValue, this.getStringValue()))
		{
			this.onValueChanged();
		}
	}
}
