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

import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerKeybind;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.hotkeys.IHotkey;

/**
 * Stolen from malilib 1.18 v0.11.4
 * original name: HotkeyedBooleanResetListener
 */
public class HotkeyedResetListener extends ConfigOptionChangeListenerKeybind
{
	private final IHotkey config;
	private final ButtonGeneric valueButton;
	private final ConfigButtonKeybind hotkeyButton;
	private final ButtonGeneric resetButton;
	private final IKeybindConfigGui host;

	public HotkeyedResetListener(IHotkey config, ButtonGeneric valueButton, ConfigButtonKeybind hotkeyButton, ButtonGeneric resetButton, IKeybindConfigGui host)
	{
		super(config.getKeybind(), hotkeyButton, valueButton, host);
		this.config = config;
		this.valueButton = valueButton;
		this.hotkeyButton = hotkeyButton;
		this.resetButton = resetButton;
		this.host = host;
	}

	@Override
	public void actionPerformedWithButton(ButtonBase button, int mouseButton)
	{
		this.config.resetToDefault();
		this.host.getButtonPressListener().actionPerformedWithButton(button, mouseButton);
		this.updateButtons();
	}

	@Override
	public void updateButtons()
	{
		this.valueButton.updateDisplayString();
		this.hotkeyButton.updateDisplayString();
		this.resetButton.setEnabled(this.config.isModified());
	}
}