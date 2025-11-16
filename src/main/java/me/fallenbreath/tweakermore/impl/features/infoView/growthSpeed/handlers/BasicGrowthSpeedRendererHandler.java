/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.impl.features.infoView.growthSpeed.handlers;

import com.google.common.collect.Lists;
import me.fallenbreath.tweakermore.util.Messenger;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.ChatFormatting;

import java.util.List;
import java.util.stream.Collectors;

import static me.fallenbreath.tweakermore.util.Messenger.*;

/**
 * Utility base class
 */
abstract class BasicGrowthSpeedRendererHandler implements GrowthSpeedRendererHandler
{
	protected static BaseComponent tr(String key, Object... args)
	{
		return Messenger.tr("tweakermore.impl.infoViewGrowthSpeed." + key,args);
	}

	protected static String round(double value, int maxDigit)
	{
		if (maxDigit == 0)
		{
			return String.valueOf(Math.round(value));
		}
		double base = Math.pow(10, maxDigit);
		return String.valueOf(Math.round(value * base) / base);
	}

	protected static BaseComponent bool(boolean value)
	{
		return bool(value, value ? ChatFormatting.GREEN : ChatFormatting.RED);
	}

	protected static BaseComponent bool(boolean value, ChatFormatting colorOverride)
	{
		return s(value ? "√" : "x", colorOverride);
	}

	protected static ChatFormatting heatColor(double value)
	{
		if (value >= 0.8) return ChatFormatting.GREEN;
		if (value >= 0.5) return ChatFormatting.YELLOW;
		if (value >= 0.2) return ChatFormatting.GOLD;
		return ChatFormatting.RED;
	}

	protected static BaseComponent pair(BaseComponent key, BaseComponent value)
	{
		return c(key, s(": ", ChatFormatting.GRAY), value);
	}

	protected static class Attributes
	{
		private final List<Attribute> attributes = Lists.newArrayList();

		public void add(Object name, Object value, boolean hideIfSingleLine)
		{
			this.attributes.add(new Attribute(tf(name), tf(value), hideIfSingleLine));
		}

		public void add(Object name, Object value)
		{
			this.add(name, value, false);
		}

		public BaseComponent toSingleLine()
		{
			return join(
					s(" "),
					this.attributes.stream().
							filter(a -> !a.hideIfSingleLine).
							map(a -> a.value).
							toArray(BaseComponent[]::new)
			);
		}

		public List<BaseComponent> toMultiLines()
		{
			return this.attributes.stream().
					map(a -> pair(a.name, a.value)).
					collect(Collectors.toList());
		}

		public void export(List<BaseComponent> lines, boolean isCrossHairPos)
		{
			if (isCrossHairPos)
			{
				lines.addAll(this.toMultiLines());
			}
			else
			{
				lines.add(this.toSingleLine());
			}
		}
	}

	protected static class Attribute
	{
		public final BaseComponent name;
		public final BaseComponent value;
		public final boolean hideIfSingleLine;

		private Attribute(BaseComponent name, BaseComponent value, boolean hideIfSingleLine)
		{
			this.name = name;
			this.value = value;
			this.hideIfSingleLine = hideIfSingleLine;
		}
	}
}
