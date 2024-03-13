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

package me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerBoxTooltipHints.builder;

import com.google.common.collect.Lists;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.Messenger;
import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.BaseText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PotionHintBuilder extends AbstractHintBuilder
{
	@Override
	@Nullable
	public BaseText build(ItemStack itemStack)
	{
		if (TweakerMoreConfigs.SHULKER_BOX_TOOLTIP_POTION_INFO_HINT.getBooleanValue())
		{
			Item item = itemStack.getItem();
			float ratio = getPotionDurationRatio(item);
			if (ratio > 0 && PotionUtil.getPotionEffects(itemStack).size() > 0)
			{
				List<Text> potionTexts = Lists.newArrayList();
				//#if MC >= 12004
				//$$ var world = net.minecraft.client.MinecraftClient.getInstance().world;
				//$$ float tickRate = world != null ? world.getTickManager().getTickRate() : 20.0f;
				//#endif
				PotionUtil.buildTooltip(
						itemStack, potionTexts, ratio
						//#if MC >= 12004
						//$$ , tickRate
						//#endif
				);

				int i = 0;
				BaseText newLine = Messenger.s("");
				for (; i < potionTexts.size(); i++)
				{
					// we don't want the "potion.whenDrank" section
					if (potionTexts.get(i).equals(newLine))
					{
						break;
					}
				}

				return buildSegments(potionTexts.subList(0, i));
			}
		}
		return null;
	}

	/**
	 * Reference: Constants in
	 * - {@link PotionItem#appendTooltip}
	 * - {@link LingeringPotionItem#appendTooltip}
	 * - {@link TippedArrowItem#appendTooltip}
	 */
	private static float getPotionDurationRatio(Item item)
	{
		if (item instanceof LingeringPotionItem)
		{
			return 0.25F;
		}
		if (item instanceof TippedArrowItem)
		{
			return 0.125F;
		}
		if (item instanceof PotionItem)
		{
			return 1.0F;
		}
		// unknown
		return -1;
	}
}
