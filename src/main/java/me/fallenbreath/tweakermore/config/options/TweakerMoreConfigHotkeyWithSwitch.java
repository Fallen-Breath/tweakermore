/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

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
	public boolean isKeybindHeld()
	{
		return this.getEnableState() && super.isKeybindHeld();
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
