package me.fallenbreath.tweakermore.config.options;

import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.hotkeys.IHotkey;

public interface IHotkeyWithSwitch extends IHotkey, IConfigBoolean
{
	boolean getEnableState();

	boolean getDefaultEnableState();

	void setEnableState(boolean value);

	default boolean isKeybindHeld()
	{
		return this.getEnableState() && this.getKeybind().isKeybindHeld();
	}

	@Deprecated
	@Override
	default boolean getBooleanValue()
	{
		return this.getEnableState();
	}

	@Deprecated
	@Override
	default boolean getDefaultBooleanValue()
	{
		return this.getDefaultEnableState();
	}

	@Deprecated
	@Override
	default void setBooleanValue(boolean value)
	{
		this.setEnableState(value);
	}
}
