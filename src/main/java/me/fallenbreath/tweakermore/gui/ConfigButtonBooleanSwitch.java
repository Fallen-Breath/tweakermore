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
