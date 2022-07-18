package me.fallenbreath.tweakermore.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.JsonUtils;
import me.fallenbreath.tweakermore.TweakerMoreMod;

public class TweakerMoreConfigHotkeyWithSwitch extends TweakerMoreConfigHotkey implements IHotkeyWithSwitch
{
	private final boolean defaultEnableState;
	private boolean enableState;

	public TweakerMoreConfigHotkeyWithSwitch(String name, boolean defaultEnableState, String defaultStorageString)
	{
		super(name, defaultStorageString);
		this.defaultEnableState = defaultEnableState;
	}

	public TweakerMoreConfigHotkeyWithSwitch(String name, boolean defaultEnableState, String defaultStorageString, KeybindSettings settings)
	{
		super(name, defaultStorageString, settings);
		this.defaultEnableState = defaultEnableState;
	}

	@Override
	public boolean isModified()
	{
		return super.isModified() || this.enableState != this.defaultEnableState;
	}

	@Override
	public void resetToDefault()
	{
		super.resetToDefault();
		this.enableState = this.defaultEnableState;
	}

	@Override
	public void setValueFromJsonElement(JsonElement element)
	{
		boolean oldState = this.getEnableState();

		super.setValueFromJsonElement(element);
		this.readExtraDataFromJson(element);

		if (oldState != this.getEnableState())
		{
			this.onValueChanged(true);
		}
	}

	private void readExtraDataFromJson(JsonElement element)
	{
		try
		{
			if (element.isJsonObject())
			{
				JsonObject obj = element.getAsJsonObject();

				if (JsonUtils.hasBoolean(obj, "enabled"))
				{
					this.enableState = obj.get("enabled").getAsBoolean();
				}
			}
		}
		catch (Exception e)
		{
			TweakerMoreMod.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
		}
	}

	@Override
	public JsonElement getAsJsonElement()
	{
		JsonElement jsonElement = super.getAsJsonElement();
		if (!jsonElement.isJsonObject())
		{
			throw new RuntimeException("super should return a json object, but " + jsonElement + " found");
		}
		jsonElement.getAsJsonObject().addProperty("enabled", this.enableState);
		return jsonElement;
	}

	@Override
	public boolean getEnableState()
	{
		return this.enableState;
	}

	@Override
	public boolean getDefaultEnableState()
	{
		return this.defaultEnableState;
	}

	@Override
	public void setEnableState(boolean value)
	{
		boolean oldValue = this.enableState;
		this.enableState = value;
		if (this.enableState != oldValue)
		{
			this.onValueChanged(false);
		}
	}
}
