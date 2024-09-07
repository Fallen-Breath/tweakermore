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

package me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerBoxItemContentHint;

import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.InventoryUtil;
import me.fallenbreath.tweakermore.util.ItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

import fi.dy.masa.malilib.util.InfoUtils;

public class ShulkerBoxItemContentHintCommon
{
	public static class Info
	{
		public boolean enabled;
		public boolean allItemSame;
		public boolean allItemSameIgnoreNbt;
		public double scale;
		public ItemStack stack;
		public double fillRatio;
	}

	public static Info prepareInformation(ItemStack itemStack)
	{
		Info info = new Info();
		info.enabled = false;
		if (!TweakerMoreConfigs.SHULKER_BOX_ITEM_CONTENT_HINT.getBooleanValue())
		{
			return info;
		}
		if (ShulkerBoxItemContentHintRenderer.isRendering.get())  // no hint if it's rendering our hint item
		{
			return info;
		}
		if (!ItemUtil.isShulkerBox(itemStack.getItem()))
		{
			return info;
		}
		Optional<DefaultedList<ItemStack>> stackList = InventoryUtil.getStoredItems(itemStack);
		if (!stackList.isPresent())
		{
			return info;
		}

		ItemStack std = null;
		info.allItemSame = true;
		info.allItemSameIgnoreNbt = true;
		for (ItemStack stack : stackList.get())
		{
			if (!stack.isEmpty())
			{
				if (std == null)
				{
					std = stack;
					continue;
				}

				boolean itemEqual = ItemStack.areItemsEqual(stack, std);
				boolean itemAndNbtEqual =
						//#if MC >= 12000
						//$$ ItemStack.canCombine(stack, std);
						//#else
						itemEqual && ItemStack.areTagsEqual(stack, std);
						//#endif

				if (!itemAndNbtEqual)
				{
					info.allItemSame = false;
				}
				if (!itemEqual)
				{
					info.allItemSameIgnoreNbt = false;
				}
			}
		}

		// If option activated and box is not custom named
		if (TweakerMoreConfigs.SHULKER_BOX_ITEM_CONTENT_HINT_CUSTOM_NAMES_OVERRIDE_ITEM.getBooleanValue() && 
		!itemStack.getName().getString().equals(itemStack.getItem().getName().getString()) )
		{
			std = getCustomNameOrDefault(itemStack, std);
		}

		if (std == null)
		{
			return info;
		}

		info.stack = std;
		info.scale = TweakerMoreConfigs.SHULKER_BOX_ITEM_CONTENT_HINT_SCALE.getDoubleValue();
		if (info.scale <= 0)
		{
			return info;
		}

		info.enabled = true;
		info.fillRatio = -1;

		int slotAmount = InventoryUtil.getInventorySlotAmount(itemStack);
		if (slotAmount != -1)
		{
			double sum = 0;
			for (ItemStack stack : stackList.get())
			{
				sum += 1.0D * stack.getCount() / stack.getMaxCount();
			}

			double ratio = sum / slotAmount;
			if (ratio >= 0 && ratio <= 1)
			{
				info.fillRatio = ratio;
			}
		}

		return info;
	}


	private static ItemStack getCustomNameOrDefault(ItemStack itemStack, ItemStack def)
	{
		ChatLogger.logToChat("Name is " + itemStack.getName().getString());
		ChatLogger.logToChat("Name of item is " + itemStack.getItem().getName().getString());

		//#if MC >= 12020
		//$$ Identifier itemId = Identifier.tryParse("", itemStack.getName().getString());
		//#else
		Identifier itemId = Identifier.tryParse(itemStack.getName().getString());
		//#endif
		
		if(itemId == null){
			//#if MC >= 12020
			//$$ Identifier itemId = Identifier.tryParse(itemStack.getName().getString());
			//#else
			itemId = Identifier.tryParse("minecraft:"+ itemStack.getName().getString());
			//#endif
		}

		if(itemId == null)
		{
			return def;
		}


		Item item = Registry.ITEM.get(itemId);

		if(item == null)
		{
			return def;
		}

		ChatLogger.logToChat("The identifier is " + itemId);
		ChatLogger.logToChat("The result of the getter is " + Registry.ITEM.get(itemId)); 
		ChatLogger.logToChat("The new itemStack is " + new ItemStack(item)); 

		return new ItemStack(item);
	}
}