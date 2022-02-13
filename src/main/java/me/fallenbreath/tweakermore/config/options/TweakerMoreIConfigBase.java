package me.fallenbreath.tweakermore.config.options;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.TweakerMoreOption;

import java.util.Optional;

public interface TweakerMoreIConfigBase extends IConfigBase
{
	String TWEAKERMORE_NAMESPACE_PREFIX = TweakerMoreMod.MOD_ID + ".config.";
	String COMMENT_SUFFIX = ".comment";
	String PRETTY_NAME_SUFFIX = ".pretty_name";

	@Override
	default String getConfigGuiDisplayName()
	{
		return StringUtils.translate(TWEAKERMORE_NAMESPACE_PREFIX + this.getName());
	}

	static String modifyDisabledOptionLabelLine(String line)
	{
		return GuiBase.TXT_DARK_RED + line + GuiBase.TXT_RST;
	}

	default Optional<TweakerMoreOption> getTweakerMoreOptionOptional()
	{
		return TweakerMoreConfigs.getOptionFromConfig(this);
	}

	default boolean isEnabled()
	{
		return this.getTweakerMoreOptionOptional().map(TweakerMoreOption::isEnabled).orElseGet(() -> {
			TweakerMoreMod.LOGGER.warn("TweakerMoreIConfigBase {} not in TweakerMoreConfigs", this);
			return true;
		});
	}
}
