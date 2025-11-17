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

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.IdentifierUtils;
import me.fallenbreath.tweakermore.util.InventoryUtils;
import me.fallenbreath.tweakermore.util.ItemUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

//#if MC >= 12006
//$$ import net.minecraft.core.component.DataComponents;
//#endif

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
		if (!ItemUtils.isShulkerBox(itemStack.getItem()))
		{
			return info;
		}
		Optional<NonNullList<ItemStack>> stackList = InventoryUtils.getStoredItems(itemStack);
		if (!stackList.isPresent())
		{
			return info;
		}

		//#if MC >= 11900
		//$$ BiFunction<ItemStack, ItemStack, Boolean> itemSameChecker = ItemStack::isSame;
		//#else
		BiFunction<ItemStack, ItemStack, Boolean> itemSameChecker = ItemStack::isSameIgnoreDurability;
		//#endif

		Predicate<ItemStack> stackFilter = stack -> true;
		if (TweakerMoreConfigs.SHULKER_BOX_ITEM_CONTENT_HINT_CUSTOM_NAMES_OVERRIDE_ITEM.getBooleanValue())
		{
			Optional<ItemStack> override = computeCustomNameOverride(itemStack);
			if (override.isPresent())
			{
				stackFilter = stack -> itemSameChecker.apply(stack, override.get());
			}
		}

		ItemStack std = null;
		info.allItemSame = true;
		info.allItemSameIgnoreNbt = true;
		for (ItemStack stack : stackList.get())
		{
			if (stack.isEmpty() || !stackFilter.test(stack))
			{
				continue;
			}

			if (std == null)
			{
				std = stack;
				continue;
			}

			boolean itemEqual = itemSameChecker.apply(stack, std);
			boolean itemAndNbtEqual =
					//#if MC >= 12000
					//$$ ItemStack.isSameItemSameTags(stack, std);
					//#else
					itemEqual && ItemStack.tagMatches(stack, std);
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

		int slotAmount = InventoryUtils.getInventorySlotAmount(itemStack);
		if (slotAmount != -1)
		{
			double sum = 0;
			for (ItemStack stack : stackList.get())
			{
				sum += 1.0D * stack.getCount() / stack.getMaxStackSize();
			}

			double ratio = sum / slotAmount;
			if (ratio >= 0 && ratio <= 1)
			{
				info.fillRatio = ratio;
			}
		}

		return info;
	}

	private static Optional<ItemStack> computeCustomNameOverride(ItemStack shulkerBoxItemStack)
	{
		//#if MC >= 12006
		//$$ if (!shulkerBoxItemStack.has(DataComponents.CUSTOM_NAME))
		//#else
		if (!shulkerBoxItemStack.hasCustomHoverName())
		//#endif
		{
			return Optional.empty();
		}

		Optional<ResourceLocation> itemIdOpt = IdentifierUtils.tryParse(shulkerBoxItemStack.getHoverName().getString());
		if (!itemIdOpt.isPresent())
		{
			return Optional.empty();
		}

		if (!Registry.ITEM.containsKey(itemIdOpt.get()))
		{
			return Optional.empty();
		}

		Item item = Registry.ITEM.
				// remapping from low version to mc1.21.3 will rename the `get` to `getEntry`, which is bad
				//#disable-remap
				get(itemIdOpt.get());
				//#enable-remap
		return Optional.of(new ItemStack(item));
	}
}