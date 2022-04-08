package me.fallenbreath.tweakermore.util;

import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class InventoryUtil
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
}
