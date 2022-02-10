/**
 * Stolen from malilib 1.18 v0.11.4
 */
package me.fallenbreath.tweakermore.gui;

import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerKeybind;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;

public class HotkeyedBooleanResetListener extends ConfigOptionChangeListenerKeybind
{
	private final IConfigResettable config;
	private final ButtonGeneric booleanButton;
	private final ConfigButtonKeybind hotkeyButton;
	private final ButtonGeneric resetButton;
	private final IKeybindConfigGui host;

	public HotkeyedBooleanResetListener(IHotkeyTogglable config, ButtonGeneric booleanButton, ConfigButtonKeybind hotkeyButton, ButtonGeneric resetButton, IKeybindConfigGui host)
	{
		super(config.getKeybind(), hotkeyButton, booleanButton, host);
		this.config = config;
		this.booleanButton = booleanButton;
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
		this.booleanButton.updateDisplayString();
		this.hotkeyButton.updateDisplayString();
		this.resetButton.setEnabled(this.config.isModified());
	}
}