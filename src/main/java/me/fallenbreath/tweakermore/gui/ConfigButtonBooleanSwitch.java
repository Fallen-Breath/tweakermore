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

package me.fallenbreath.tweakermore.gui;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.config.options.IHotkeyWithSwitch;

public class ConfigButtonBooleanSwitch extends ButtonGeneric
{
	private final IHotkeyWithSwitch config;

	public ConfigButtonBooleanSwitch(int x, int y, int width, int height, IHotkeyWithSwitch config)
	{
		super(x, y, width, height, "");
		this.config = config;
		this.updateDisplayString();
	}

	@Override
	protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
	{
		this.config.toggleBooleanValue();
		this.updateDisplayString();

		return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
	}

	@Override
	public void updateDisplayString()
	{
		if (this.config.getEnableState())
		{
			this.displayString = GuiBase.TXT_DARK_GREEN + StringUtils.translate("tweakermore.gui.element.config_button_boolean_switch.enabled") + GuiBase.TXT_RST;
		}
		else
		{
			this.displayString = GuiBase.TXT_DARK_RED + StringUtils.translate("tweakermore.gui.element.config_button_boolean_switch.disabled") + GuiBase.TXT_RST;
		}
	}
}
