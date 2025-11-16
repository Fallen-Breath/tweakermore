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

package me.fallenbreath.tweakermore.impl.features.autoContainerProcess.processors;

import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigBooleanHotkeyed;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.List;

public class ContainerFiller implements IContainerProcessor
{
	@Override
	public TweakerMoreConfigBooleanHotkeyed getConfig()
	{
		return TweakerMoreConfigs.AUTO_FILL_CONTAINER;
	}

	@Override
	public ProcessResult process(LocalPlayer player, AbstractContainerScreen<?> containerScreen, List<Slot> allSlots, List<Slot> playerInvSlots, List<Slot> containerInvSlots)
	{
		if (ContainerProcessorUtils.shouldSkipForEnderChest(containerScreen, TweakerMoreConfigs.AUTO_FILL_CONTAINER_IGNORE_ENDER_CHEST))
		{
			return ProcessResult.skipped();
		}

		Slot bestSlot = null;
		long maxCount = TweakerMoreConfigs.AUTO_FILL_CONTAINER_THRESHOLD.getIntegerValue() - 1;
		for (Slot slot : playerInvSlots)
			if (slot.hasItem() && containerInvSlots.get(0).mayPlace(slot.getItem()))
			{
				long cnt = playerInvSlots.stream().filter(slt -> InventoryUtils.areStacksEqual(slot.getItem(), slt.getItem())).count();
				if (cnt > maxCount)
				{
					maxCount = cnt;
					bestSlot = slot;
				}
				else if (cnt == maxCount && bestSlot != null && !InventoryUtils.areStacksEqual(slot.getItem(), bestSlot.getItem()))
				{
					bestSlot = null;
				}
			}
		if (bestSlot != null && !allSlots.isEmpty())
		{
			Component stackName = bestSlot.getItem().getHoverName();
			InventoryUtils.tryMoveStacks(bestSlot, containerScreen, true, true, false);
			long amount = containerInvSlots.stream().filter(Slot::hasItem).count(), total = containerInvSlots.size();
			boolean isFull = AbstractContainerMenu.getRedstoneSignalFromContainer(containerInvSlots.get(0).container) >= 15;
			String percentage = String.format("%s%d/%d%s", isFull ? ChatFormatting.GREEN : ChatFormatting.GOLD, amount, total, ChatFormatting.RESET);
			InfoUtils.printActionbarMessage("tweakermore.impl.autoFillContainer.container_filled", containerScreen.getTitle(), stackName, percentage);
			return ProcessResult.terminated();
		}
		else
		{
			InfoUtils.printActionbarMessage("tweakermore.impl.autoFillContainer.best_slot_not_found");
			return ProcessResult.skipped();
		}
	}
}
