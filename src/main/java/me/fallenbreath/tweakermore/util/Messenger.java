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

import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Messenger
{
	/*
	 * ----------------------------
	 *    Text Factories - Basic
	 * ----------------------------
	 */

	// simple Text
	public static BaseText s(Object text)
	{
		return
				//#if MC >= 11900
				//$$ Text.literal
				//#else
				new LiteralText
				//#endif
						(text.toString());
	}

	// textfy
	public static BaseText tf(Object text)
	{
		return text instanceof BaseText ? (BaseText)text : s(text);
	}

	// compound text
	public static BaseText c(Object ... fields)
	{
		BaseText text = s("");
		for (Object field : fields)
		{
			text.append(Messenger.tf(field));
		}
		return text;
	}

	// Simple Text with formatting
	public static BaseText s(Object text, Formatting textFormatting)
	{
		return formatting(s(text), textFormatting);
	}

	// Translation Text
	public static BaseText tr(String key, Object ... args)
	{
		return
				//#if MC >= 11900
				//$$ Text.translatable
				//#else
				new TranslatableText
				//#endif
						(key, args);
	}

	// Fancy text
	// A copy will be made to make sure the original displayText will not be modified
	public static BaseText fancy(@NotNull BaseText displayText, @Nullable BaseText hoverText, @Nullable ClickEvent clickEvent)
	{
		BaseText text = copy(displayText);
		if (hoverText != null)
		{
			hover(text, hoverText);
		}
		if (clickEvent != null)
		{
			click(text, clickEvent);
		}
		return text;
	}

	public static BaseText join(BaseText joiner, BaseText... items)
	{
		BaseText text = s("");
		for (int i = 0; i < items.length; i++)
		{
			if (i > 0)
			{
				text.append(joiner);
			}
			text.append(items[i]);
		}
		return text;
	}

	/*
	 * --------------------
	 *    Text Modifiers
	 * --------------------
	 */

	public static BaseText hover(BaseText text, HoverEvent hoverEvent)
	{
		//#if MC >= 11600
		//$$ style(text, text.getStyle().withHoverEvent(hoverEvent));
		//#else
		text.getStyle().setHoverEvent(hoverEvent);
		//#endif
		return text;
	}

	public static BaseText hover(BaseText text, BaseText hoverText)
	{
		return hover(
				text,
				//#if MC >= 12105
				//$$ new HoverEvent.ShowText(hoverText)
				//#else
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)
				//#endif
		);
	}

	public static BaseText click(BaseText text, ClickEvent clickEvent)
	{
		//#if MC >= 11600
		//$$ style(text, text.getStyle().withClickEvent(clickEvent));
		//#else
		text.getStyle().setClickEvent(clickEvent);
		//#endif
		return text;
	}

	public static BaseText formatting(BaseText text, Formatting... formattings)
	{
		text.formatted(formattings);
		return text;
	}

	public static BaseText style(BaseText text, Style style)
	{
		text.setStyle(style);
		return text;
	}

	public static BaseText copy(BaseText text)
	{
		//#if MC >= 11900
		//$$ return text.copy();
		//#elseif MC >= 11600
		//$$ return (BaseText)text.shallowCopy();
		//#else
		return (BaseText)text.deepCopy();
		//#endif
	}
}
