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

package me.fallenbreath.tweakermore.impl.mc_tweaks.tadpoleBucketTooltipHints;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.Messenger;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.item.ItemStack;

import java.util.List;

//#if MC >= 12108
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//#else
import me.fallenbreath.tweakermore.util.render.context.RenderGlobals;
//#endif

//#if MC >= 12006
//$$ import net.minecraft.core.component.DataComponents;
//$$ import net.minecraft.world.item.component.CustomData;
//#endif

public class TadpoleBucketToolTipEnhancer
{
	public static void applyLeftTimeHint(ItemStack bucket, List<Component> tooltip)
	{
		if (
				//#if MC >= 12108
				//$$ !RenderSystem.isOnRenderThread()
				//#else
				!RenderGlobals.isOnRenderThread()
				//#endif
		)
		{
			// Do nothing in case it's called from non-render thread by whatever mod
			// see also: https://github.com/Fallen-Breath/tweakermore/issues/138
			return;
		}
		if (!TweakerMoreConfigs.TADPOLE_BUCKET_GROWTH_LEFT_TIME.getBooleanValue())
		{
			return;
		}
		if (tooltip.isEmpty())
		{
			return;
		}

		int ticks = getTadpoleLeftTicks(bucket);
		String time = formatTicksToMinuteSecond(ticks);

		Component timeText = Messenger.s(time, ChatFormatting.GRAY);

		// let timeText be rendered right-aligned
		String spacing = " ";
		Component firstLine = Messenger.c(tooltip.get(0), spacing, timeText);
		Font textRenderer = Minecraft.getInstance().font;

		int maxWidth = tooltip.stream().mapToInt(textRenderer::width).max().orElse(0);

		while (true)
		{
			List<Component> siblings = firstLine.getSiblings();
			spacing += " ";
			Component prevSpacing = siblings.get(1);
			siblings.set(1, Messenger.s(spacing));
			if (textRenderer.width(firstLine) > maxWidth)
			{
				siblings.set(1, prevSpacing);  // rollback
				break;
			}
		}
		tooltip.set(0, firstLine);
	}

	private static int getTadpoleLeftTicks(ItemStack bucket)
	{
		CompoundTag tag =
				//#if MC < 12006
				bucket.getOrCreateTag();
		//#else
		//$$ bucket.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY).copyTag();
		//#endif
		int age = tag.getInt("Age")
				//#if MC >= 12105
				//$$ .orElse(0)
				//#endif
				;
		return Math.max(Tadpole.ticksToBeFrog - age, 0);
	}

	private static String formatTicksToMinuteSecond(int ticks)
	{
		int totalSeconds = Mth.ceil((double)ticks / 20);
		int minutes = totalSeconds / 60;
		int seconds = totalSeconds % 60;
		return String.format("%d:%02d", minutes, seconds);
	}
}
