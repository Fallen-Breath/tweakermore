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

package me.fallenbreath.tweakermore.impl.features.tweakmAutoContainerProcess.processors;

import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigBooleanHotkeyed;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ContainerFiller implements IContainerProcessor
{
	@Override
	public TweakerMoreConfigBooleanHotkeyed getConfig()
	{
		return TweakerMoreConfigs.TWEAKM_AUTO_FILL_CONTAINER;
	}

	@Override
	public boolean process(ClientPlayerEntity player, ContainerScreen<?> containerScreen, List<Slot> allSlots, List<Slot> playerInvSlots, List<Slot> containerInvSlots)
	{
		Slot bestSlot = null;
		long maxCount = TweakerMoreConfigs.AUTO_FILL_CONTAINER_THRESHOLD.getIntegerValue() - 1;
		for (Slot slot : playerInvSlots)
			if (slot.hasStack() && containerInvSlots.get(0).canInsert(slot.getStack()))
			{
				long cnt = playerInvSlots.stream().filter(slt -> InventoryUtils.areStacksEqual(slot.getStack(), slt.getStack())).count();
				if (cnt > maxCount)
				{
					maxCount = cnt;
					bestSlot = slot;
				}
				else if (cnt == maxCount && bestSlot != null && !InventoryUtils.areStacksEqual(slot.getStack(), bestSlot.getStack()))
				{
					bestSlot = null;
				}
			}
		if (bestSlot != null && !allSlots.isEmpty())
		{
			Text stackName = bestSlot.getStack().getName();
			InventoryUtils.tryMoveStacks(bestSlot, containerScreen, true, true, false);
			long amount = containerInvSlots.stream().filter(Slot::hasStack).count(), total = containerInvSlots.size();
			boolean isFull = Container.calculateComparatorOutput(containerInvSlots.get(0).inventory) >= 15;
			String percentage = String.format("%s%d/%d%s", isFull ? Formatting.GREEN : Formatting.GOLD, amount, total, Formatting.RESET);
			InfoUtils.printActionbarMessage("tweakermore.impl.tweakmAutoFillContainer.container_filled", containerScreen.getTitle(), stackName, percentage);
			return true;
		}
		else
		{
			InfoUtils.printActionbarMessage("tweakermore.impl.tweakmAutoFillContainer.best_slot_not_found");
			return false;
		}
	}
}
