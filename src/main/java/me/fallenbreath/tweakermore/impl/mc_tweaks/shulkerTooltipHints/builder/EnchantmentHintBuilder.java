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

package me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerTooltipHints.builder;

import com.google.common.collect.Lists;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.BaseText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnchantmentHintBuilder extends AbstractHintBuilder
{
	@Override
	@Nullable
	public BaseText build(ItemStack itemStack)
	{
		if (TweakerMoreConfigs.SHULKER_TOOLTIP_ENCHANTMENT_HINT.getBooleanValue())
		{
			List<Text> enchantmentTexts = Lists.newArrayList();
			ListTag enchantmentTag = itemStack.getItem() instanceof EnchantedBookItem ? EnchantedBookItem.getEnchantmentTag(itemStack) : itemStack.getEnchantments();
			ItemStack.appendEnchantments(enchantmentTexts, enchantmentTag);
			return buildSegments(enchantmentTexts);
		}
		return null;
	}
}
