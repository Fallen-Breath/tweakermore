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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

//#if MC >= 12100
//$$ import net.minecraft.core.component.DataComponentPatch;
//$$ import net.minecraft.Util;
//#endif

//#if MC >= 12006
//$$ import net.minecraft.commands.arguments.item.ItemInput;
//#endif

public class ItemDataCopier
{
	public static void copyItemData()
	{
		Minecraft mc = Minecraft.getInstance();
		if (mc.screen instanceof AbstractContainerScreen)
		{
			Slot slot = ((ContainerScreenAccessor)mc.screen).getFocusedSlot();
			if (slot != null && slot.hasItem() && mc.level != null)
			{
				ItemStack itemStack = slot.getItem();

				//#if MC >= 12006
				//$$ var arg = new ItemInput(
				//$$ 		itemStack.getItemHolder(),
				//$$ 		//#if MC >= 12100
				//$$ 		//$$ Util.make(() -> {
				//$$ 		//$$ 	var builder = DataComponentPatch.builder();
				//$$ 		//$$ 	itemStack.getComponents().forEach(builder::add);
				//$$ 		//$$ 	return builder.build();
				//$$ 		//$$ })
				//$$ 		//#else
				//$$ 		itemStack.getComponents()
				//$$ 		//#endif
				//$$ );
				//$$ String command = String.format("/give @s %s", arg.serialize(mc.level.registryAccess()));
				//#else
				String command = String.format("/give @s %s", itemStack.getItem());
				CompoundTag nbt = itemStack.getTag();
				if (nbt != null && !nbt.isEmpty())
				{
					command += nbt.toString();
				}
				//#endif

				mc.keyboardHandler.setClipboard(command);
				InfoUtils.printActionbarMessage("tweakermore.impl.copyItemDataToClipBoard.item_copied", itemStack.getHoverName());
			}
		}
	}
}
