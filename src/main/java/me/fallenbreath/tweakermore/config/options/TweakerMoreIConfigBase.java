package me.fallenbreath.tweakermore.config.options;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.TweakerMoreOption;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface TweakerMoreIConfigBase extends IConfigBase
{
	String TWEAKERMORE_NAMESPACE_PREFIX = TweakerMoreMod.MOD_ID + ".config.";
	String COMMENT_SUFFIX = ".comment";
	String PRETTY_NAME_SUFFIX = ".pretty_name";

	void onValueChanged(boolean fromFile);

	default void setCommentModifier(@Nullable Function<String, String> commentModifier)
	{
		this.getTweakerMoreOption().setCommentModifier(commentModifier);
	}

	default String getCommentNoFooter()
	{
		TweakerMoreOption option = this.getTweakerMoreOption();
		option.setAppendFooterFlag(false);
		try
		{
			return this.getComment();
		}
		finally
		{
			option.setAppendFooterFlag(true);
		}
	}

	default void updateStatisticOnUse()
	{
		this.getTweakerMoreOption().getStatistic().onConfigUsed();
	}

	@Override
	default String getConfigGuiDisplayName()
	{
		return StringUtils.translate(TWEAKERMORE_NAMESPACE_PREFIX + this.getName());
	}

	default TweakerMoreOption getTweakerMoreOption()
	{
		return TweakerMoreConfigs.getOptionFromConfig(this).orElseThrow(() -> new RuntimeException("TweakerMoreIConfigBase " + this + " not in TweakerMoreConfigs"));
	}

	default Function<String, String> getGuiDisplayLineModifier()
	{
		TweakerMoreOption tweakerMoreOption = this.getTweakerMoreOption();
		if (!tweakerMoreOption.isEnabled())
		{
			return line -> GuiBase.TXT_DARK_RED + line + GuiBase.TXT_RST;
		}
		if (tweakerMoreOption.isDebug())
		{
			return line -> GuiBase.TXT_BLUE + line + GuiBase.TXT_RST;
		}
		if (tweakerMoreOption.isDevOnly())
		{
			return line -> GuiBase.TXT_LIGHT_PURPLE + line + GuiBase.TXT_RST;
		}
		return line -> line;
	}
}
