package me.fallenbreath.tweakermore.config.options;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.TweakerMoreOption;

public interface TweakerMoreIConfigBase extends IConfigBase
{
	String TWEAKERMORE_NAMESPACE_PREFIX = TweakerMoreMod.MOD_ID + ".";
	String COMMENT_SUFFIX = ".comment";
	String PRETTY_NAME_SUFFIX = ".pretty_name";

	/**
	 * Will be translated when passing to {@link fi.dy.masa.malilib.gui.widgets.WidgetLabel}
	 */
	@Override
	default String getConfigGuiDisplayName()
	{
		return TWEAKERMORE_NAMESPACE_PREFIX + this.getName();
	}

	static String modifyDisabledOptionLabelLine(String line)
	{
		return GuiBase.TXT_DARK_RED + line + GuiBase.TXT_RST;
	}

	default boolean isEnabled()
	{
		return TweakerMoreConfigs.getOptionFromConfig(this).map(TweakerMoreOption::isEnabled).orElseGet(() -> {
			TweakerMoreMod.LOGGER.warn("TweakerMoreIConfigBase {} not in TweakerMoreConfigs", this);
			return true;
		});
	}
}
