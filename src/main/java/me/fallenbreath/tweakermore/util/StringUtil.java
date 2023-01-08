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

package me.fallenbreath.tweakermore.util;

import fi.dy.masa.malilib.gui.GuiBase;
import me.fallenbreath.tweakermore.config.options.TweakerMoreIConfigBase;
import net.minecraft.util.Formatting;

public class StringUtil
{
	public static String removeFormattingCode(String string)
	{
		return Formatting.strip(string);
	}

	public static String configsToListLines(Iterable<? extends TweakerMoreIConfigBase> configs)
	{
		StringBuilder builder = new StringBuilder();
		boolean isFirst = true;
		for (TweakerMoreIConfigBase config : configs)
		{
			String id = config.getName();
			String name = config.getConfigGuiDisplayName();

			if (!isFirst)
			{
				builder.append("\n");
			}
			isFirst = false;

			builder.append(GuiBase.TXT_GRAY).append("- ");
			builder.append(GuiBase.TXT_RST).append(name);

			if (!id.equals(name))
			{
				builder.append(GuiBase.TXT_GRAY).append(" (").append(id).append(")").append(GuiBase.TXT_RST);
			}
		}
		return builder.toString();
	}
}
