package me.fallenbreath.tweakermore.config.options;

import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;

import javax.annotation.Nullable;

public class TweakerMoreConfigHotkey extends ConfigHotkey implements TweakerMoreIConfigBase
{
	public TweakerMoreConfigHotkey(String name, String defaultStorageString)
	{
		super(name, defaultStorageString, TWEAKERMORE_NAMESPACE_PREFIX + name + COMMENT_SUFFIX);
	}

	public TweakerMoreConfigHotkey(String name, String defaultStorageString, KeybindSettings settings)
	{
		super(name, defaultStorageString, settings, TWEAKERMORE_NAMESPACE_PREFIX + name + COMMENT_SUFFIX);
	}

	/**
	 * Use this instead of {@code getKeybind().setCallback} directly
	 * So the config statistic can be updated correctly
	 */
	public void setCallBack(@Nullable IHotkeyCallback callback)
	{
		// don't count OPEN_TWEAKERMORE_CONFIG_GUI cuz technically it's just a shortcut option instead of a feature option
		if (callback == null || this == TweakerMoreConfigs.OPEN_TWEAKERMORE_CONFIG_GUI)
		{
			this.getKeybind().setCallback(callback);
		}
		else
		{
			this.getKeybind().setCallback((action, key) -> {
				boolean ret = callback.onKeyAction(action, key);
				this.updateStatisticOnUse();
				return ret;
			});
		}
	}

	@Override
	public void onValueChanged()
	{
		this.onValueChanged(true);
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
