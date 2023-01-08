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
