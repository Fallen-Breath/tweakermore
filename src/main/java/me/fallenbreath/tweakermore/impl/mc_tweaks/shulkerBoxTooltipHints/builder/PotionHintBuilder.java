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
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//#if MC >= 12006
//$$ import net.minecraft.core.component.DataComponents;
//$$ import net.minecraft.world.item.Item;
//#endif

public class PotionHintBuilder extends AbstractHintBuilder
{
	@Override
	@Nullable
	public BaseComponent build(
			//#if MC >= 12006
			//$$ Item.TooltipContext context,
			//#endif
			ItemStack itemStack
	)
	{
		if (TweakerMoreConfigs.SHULKER_BOX_TOOLTIP_POTION_INFO_HINT.getBooleanValue())
		{
			Item item = itemStack.getItem();
			float ratio = getPotionDurationRatio(item);

			//#if MC >= 12006
			//$$ var potions = itemStack.get(DataComponents.POTION_CONTENTS);
			//$$ if (ratio > 0 && potions != null)
			//#else
			if (ratio > 0 && !PotionUtils.getMobEffects(itemStack).isEmpty())
			//#endif

			{
				List<Component> potionTexts = Lists.newArrayList();
				//#if MC >= 12004
				//$$ var world = net.minecraft.client.MinecraftClient.getInstance().world;
				//$$ float tickRate = world != null ? world.getTickManager().getTickRate() : 20.0f;
				//#endif

				//#if MC >= 12006
				//$$ PotionContentsComponent.buildTooltip(potions.getEffects(), potionTexts::add, ratio, tickRate);
				//#elseif MC >= 12006
				//$$ potions.buildTooltip(potionTexts::add, ratio, tickRate);
				//#else
				PotionUtils.addPotionTooltip(
						itemStack, potionTexts, ratio
						//#if MC >= 12004
						//$$ , tickRate
						//#endif
				);
				//#endif

				int i = 0;
				BaseComponent newLine = Messenger.s("");
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
