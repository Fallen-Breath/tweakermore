package me.fallenbreath.tweakermore.config.options;

import com.google.gson.JsonElement;
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;

public class TweakerMoreConfigBooleanHotkeyed extends ConfigBooleanHotkeyed implements TweakerMoreIConfigBase
{
	public TweakerMoreConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey)
	{
		super(name, defaultValue, defaultHotkey, TWEAKERMORE_NAMESPACE_PREFIX + name + COMMENT_SUFFIX, TWEAKERMORE_NAMESPACE_PREFIX + name + PRETTY_NAME_SUFFIX);
	}

	@Override
	public void setValueFromJsonElement(JsonElement element)
	{
		boolean oldValue = this.getBooleanValue();

		super.setValueFromJsonElement(element);

		if (oldValue != this.getBooleanValue())
		{
			this.onValueChanged(true);
		}
	}

	@Override
	public void onValueChanged()
	{
		this.onValueChanged(false);
	}

	@Override
	public void onValueChanged(boolean fromFile)
	{
		super.onValueChanged();
		if (!fromFile)
		{
			this.updateStatisticOnUse();
		}
	}
}
