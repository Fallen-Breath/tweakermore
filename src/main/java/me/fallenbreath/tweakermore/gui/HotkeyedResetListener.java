/**
 * Stolen from malilib 1.18 v0.11.4
 * original name: HotkeyedBooleanResetListener
 */
package me.fallenbreath.tweakermore.gui;

import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerKeybind;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.hotkeys.IHotkey;

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