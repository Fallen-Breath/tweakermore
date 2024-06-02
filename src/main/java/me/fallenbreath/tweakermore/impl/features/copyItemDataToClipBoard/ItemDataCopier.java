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

package me.fallenbreath.tweakermore.impl.features.copyItemDataToClipBoard;

import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.mixins.tweaks.features.copyItemData.ContainerScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

//#if MC >= 12006
//$$ import net.minecraft.command.argument.ItemStackArgument;
//#endif

public class ItemDataCopier
{
	public static void copyItemData()
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.currentScreen instanceof ContainerScreen)
		{
			Slot slot = ((ContainerScreenAccessor)mc.currentScreen).getFocusedSlot();
			if (slot != null && slot.hasStack() && mc.world != null)
			{
				ItemStack itemStack = slot.getStack();

				//#if MC >= 12006
				//$$ var arg = new ItemStackArgument(itemStack.getRegistryEntry(), itemStack.getComponents());
				//$$ String command = String.format("/give @s %s", arg.asString(mc.world.getRegistryManager()));
				//#else
				String command = String.format("/give @s %s", itemStack.getItem());
				CompoundTag nbt = itemStack.getTag();
				if (nbt != null && !nbt.isEmpty())
				{
					command += nbt.toString();
				}
				//#endif

				mc.keyboard.setClipboard(command);
				InfoUtils.printActionbarMessage("tweakermore.impl.copyItemDataToClipBoard.item_copied", itemStack.getName());
			}
		}
	}
}
