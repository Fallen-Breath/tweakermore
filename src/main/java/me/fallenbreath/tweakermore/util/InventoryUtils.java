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

package me.fallenbreath.tweakermore.util;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

import java.util.Optional;

public class InventoryUtils
{
	public static int getInventorySlotAmount(ItemStack itemStack)
	{
		if (itemStack.getItem() instanceof BlockItem)
		{
			Block block = ((BlockItem)itemStack.getItem()).getBlock();
			if (block instanceof ShulkerBoxBlock || block instanceof ChestBlock || block instanceof BarrelBlock)
			{
				return 27;
			}
			else if (block instanceof AbstractFurnaceBlock)
			{
				return 3;
			}
			else if (block instanceof DispenserBlock)
			{
				return 9;
			}
			else if (block instanceof HopperBlock || block instanceof BrewingStandBlock)
			{
				return 5;
			}
		}
		return -1;
	}

	public static Optional<DefaultedList<ItemStack>> getStoredItems(ItemStack itemStack)
	{
		int slotAmount = InventoryUtils.getInventorySlotAmount(itemStack);
		if (slotAmount == -1)
		{
			return Optional.empty();
		}
		return Optional.ofNullable(fi.dy.masa.malilib.util.InventoryUtils.getStoredItems(itemStack, slotAmount));
	}
}
