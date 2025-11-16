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

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
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
	public static BaseComponent s(Object text)
	{
		return
				//#if MC >= 11900
				//$$ Component.literal
				//#else
				new TextComponent
				//#endif
						(text.toString());
	}

	// textfy
	public static BaseComponent tf(Object text)
	{
		return text instanceof BaseComponent ? (BaseComponent)text : s(text);
	}

	// compound text
	public static BaseComponent c(Object ... fields)
	{
		BaseComponent text = s("");
		for (Object field : fields)
		{
			text.append(Messenger.tf(field));
		}
		return text;
	}

	// Simple Text with formatting
	public static BaseComponent s(Object text, ChatFormatting textFormatting)
	{
		return formatting(s(text), textFormatting);
	}

	// Translation Text
	public static BaseComponent tr(String key, Object ... args)
	{
		return
				//#if MC >= 11900
				//$$ Component.translatable
				//#else
				new TranslatableComponent
				//#endif
						(key, args);
	}

	// Fancy text
	// A copy will be made to make sure the original displayText will not be modified
	public static BaseComponent fancy(@NotNull BaseComponent displayText, @Nullable BaseComponent hoverText, @Nullable ClickEvent clickEvent)
	{
		BaseComponent text = copy(displayText);
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

	public static BaseComponent join(BaseComponent joiner, BaseComponent... items)
	{
		BaseComponent text = s("");
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

	public static BaseComponent hover(BaseComponent text, HoverEvent hoverEvent)
	{
		//#if MC >= 11600
		//$$ style(text, text.getStyle().withHoverEvent(hoverEvent));
		//#else
		text.getStyle().setHoverEvent(hoverEvent);
		//#endif
		return text;
	}

	public static BaseComponent hover(BaseComponent text, BaseComponent hoverText)
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

	public static BaseComponent click(BaseComponent text, ClickEvent clickEvent)
	{
		//#if MC >= 11600
		//$$ style(text, text.getStyle().withClickEvent(clickEvent));
		//#else
		text.getStyle().setClickEvent(clickEvent);
		//#endif
		return text;
	}

	public static BaseComponent formatting(BaseComponent text, ChatFormatting... formattings)
	{
		text.withStyle(formattings);
		return text;
	}

	public static BaseComponent style(BaseComponent text, Style style)
	{
		text.setStyle(style);
		return text;
	}

	public static BaseComponent copy(BaseComponent text)
	{
		//#if MC >= 11900
		//$$ return text.copy();
		//#elseif MC >= 11600
		//$$ return (BaseComponent)text.copy();
		//#else
		return (BaseComponent)text.deepCopy();
		//#endif
	}
}
