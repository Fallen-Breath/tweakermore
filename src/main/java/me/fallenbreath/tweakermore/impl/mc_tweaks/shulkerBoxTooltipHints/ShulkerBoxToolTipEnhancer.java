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

package me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerBoxTooltipHints;

import com.google.common.collect.ImmutableList;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerBoxTooltipHints.builder.PotionHintBuilder;
import me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerBoxTooltipHints.builder.EnchantmentHintBuilder;
import me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerBoxTooltipHints.builder.AbstractHintBuilder;
import me.fallenbreath.tweakermore.util.InventoryUtils;
import me.fallenbreath.tweakermore.util.Messenger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;

//#if MC >= 12006
//$$ import net.minecraft.item.Item;
//#endif

//#if MC >= 11600
//$$ import net.minecraft.text.MutableText;
//#endif

public class ShulkerBoxToolTipEnhancer
{
	private static final List<AbstractHintBuilder> HINT_BUILDERS = ImmutableList.of(
			new EnchantmentHintBuilder(),
			new PotionHintBuilder()
	);

	public static void appendContentHints(
			//#if MC >= 12006
			//$$ Item.TooltipContext context,
			//#endif
			ItemStack itemStack,
			//#if MC >= 11600
			//$$ MutableText text
			//#else
			Text text
			//#endif
	)
	{
		HINT_BUILDERS.stream().
				map(builder -> builder.build(
						//#if MC >= 12006
						//$$ context,
						//#endif
						itemStack
				)).
				filter(Objects::nonNull).
				forEach(text::append);
	}

	public static void applyFillLevelHint(ItemStack skulker, List<Text> tooltip)
	{
		if (TweakerMoreConfigs.SHULKER_BOX_TOOLTIP_FILL_LEVEL_HINT.getBooleanValue() && tooltip.size() > 0)
		{
			int slotAmount = InventoryUtils.getInventorySlotAmount(skulker);
			if (slotAmount == -1)
			{
				return;
			}

			DefaultedList<ItemStack> stackList = fi.dy.masa.malilib.util.InventoryUtils.getStoredItems(skulker, slotAmount);
			if (stackList.isEmpty())
			{
				return;
			}

			double sum = 0;
			for (ItemStack stack : stackList)
			{
				sum += 1.0D * stack.getCount() / stack.getMaxCount();
			}

			double ratio = sum / slotAmount;
			Formatting color = ratio >= 1.0D ? Formatting.DARK_GREEN : Formatting.GRAY;
			Text fillLevelText = Messenger.s(String.format("%.2f%%", 100 * ratio), color);

			// let fillLevelText be rendered right-aligned
			String spacing = " ";
			Text firstLine = Messenger.c(tooltip.get(0), spacing, fillLevelText);
			TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

			ToIntFunction<Text> textLenCalc = text ->
					//#if MC >= 11600
					//$$ textRenderer.getWidth(text);
					//#else
					textRenderer.getStringWidth(text.asFormattedString());
					//#endif

			int maxWidth = tooltip.stream().mapToInt(textLenCalc).max().orElse(0);

			while (true)
			{
				List<Text> siblings = firstLine.getSiblings();
				spacing += " ";
				Text prevSpacing = siblings.get(1);
				siblings.set(1, Messenger.s(spacing));
				if (textLenCalc.applyAsInt(firstLine) > maxWidth)
				{
					siblings.set(1, prevSpacing);  // rollback
					break;
				}
			}
			tooltip.set(0, firstLine);
		}
	}
}
