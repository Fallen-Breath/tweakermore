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
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//#if MC >= 12006
//$$ import net.minecraft.core.component.DataComponents;
//$$ import net.minecraft.world.item.enchantment.ItemEnchantments;
//$$ import net.minecraft.world.item.TooltipFlag;
//$$ import net.minecraft.world.item.Item;
//#else
import net.minecraft.world.item.EnchantedBookItem;
//#endif

public class EnchantmentHintBuilder extends AbstractHintBuilder
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
		if (TweakerMoreConfigs.SHULKER_BOX_TOOLTIP_ENCHANTMENT_HINT.getBooleanValue())
		{
			List<Component> enchantmentTexts = Lists.newArrayList();

			//#if MC >= 12006
			//$$ var enchantmentTag = itemStack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.DEFAULT);
			//$$ if (enchantmentTag == ItemEnchantments.DEFAULT)
			//$$ {
			//$$ 	enchantmentTag = itemStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.DEFAULT);
			//$$ }
			//$$ enchantmentTag.appendTooltip(
			//$$ 		context, enchantmentTexts::add, TooltipFlag.ADVANCED
			//$$ 		//#if MC >= 12105
			//$$ 		//$$ , itemStack
			//$$ 		//#endif
			//$$ );
			//#else
			ListTag enchantmentTag = itemStack.getItem() instanceof EnchantedBookItem ? EnchantedBookItem.getEnchantments(itemStack) : itemStack.getEnchantmentTags();
			ItemStack.appendEnchantmentNames(enchantmentTexts, enchantmentTag);
			//#endif

			return buildSegments(enchantmentTexts);
		}
		return null;
	}
}
