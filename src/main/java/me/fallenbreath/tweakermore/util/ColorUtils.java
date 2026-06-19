/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

import java.util.Objects;

//#if MC >= 26.2
//$$ import net.minecraft.network.chat.TextColor;
//#else
import net.minecraft.ChatFormatting;
//#endif

public class ColorUtils
{
	public static int getRedColor()
	{
		//#if MC >= 26.2
		//$$ return TextColor.RED.getValue();
		//#else
		return Objects.requireNonNull(ChatFormatting.RED.getColor());
		//#endif
	}

	public static int getDarkRedColor()
	{
		//#if MC >= 26.2
		//$$ return TextColor.DARK_RED.getValue();
		//#else
		return Objects.requireNonNull(ChatFormatting.DARK_RED.getColor());
		//#endif
	}
}
