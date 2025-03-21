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
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.TweakerMoreMod;

/**
 * OptionList with a hotkey to cycle the value
 */
public class TweakerMoreConfigOptionListHotkeyed extends TweakerMoreConfigOptionList implements IOptionListHotkeyed
{
	protected final IKeybind keybind;

	public TweakerMoreConfigOptionListHotkeyed(String name, IConfigOptionListEntry defaultValue, String defaultHotkey, KeybindSettings settings)
	{
		super(name, defaultValue);

		this.keybind = KeybindMulti.fromStorageString(defaultHotkey, settings);
		this.keybind.setCallback(this::onHotkey);
	}

	public TweakerMoreConfigOptionListHotkeyed(String name, IConfigOptionListEntry defaultValue, String defaultHotkey)
	{
		this(name, defaultValue, defaultHotkey, KeybindSettings.DEFAULT);
	}

	private boolean onHotkey(KeyAction keyAction, IKeybind iKeybind)
	{
		IConfigOptionListEntry newValue = this.getOptionListValue().cycle(true);
		this.setOptionListValue(newValue);
		InfoUtils.printActionbarMessage(
				"tweakermore.config_type.option_list_hotkeyed.cycled_message",
				this.getName(),
				GuiBase.TXT_GOLD + newValue.getDisplayName() + GuiBase.TXT_RST
		);

		return true;
	}

	@Override
	public IKeybind getKeybind()
	{
		return this.keybind;
	}

	@Override
	public void resetToDefault()
	{
		super.resetToDefault();

		this.keybind.resetToDefault();
	}

	@Override
	public void setValueFromJsonElement(JsonElement element)
	{
		IConfigOptionListEntry oldValue = this.getOptionListValue();

		try
		{
			if (element.isJsonObject())
			{
				JsonObject obj = element.getAsJsonObject();
				this.setValueFromString(obj.get("value").getAsString());
				this.getKeybind().setValueFromString(obj.get("keybind").getAsString());
			}
			// a malilib OptionList
			else if (element.isJsonPrimitive())
			{
				this.setValueFromString(element.getAsString());
			}
		}
		catch (Exception e)
		{
			TweakerMoreMod.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
		}

		if (oldValue != this.getOptionListValue())
		{
			this.onValueChanged(true);
		}
	}

	@Override
	public JsonElement getAsJsonElement()
	{
		JsonObject obj = new JsonObject();
		obj.addProperty("value", this.getOptionListValue().getStringValue());
		obj.addProperty("keybind", this.getKeybind().getStringValue());
		return obj;
	}
}
