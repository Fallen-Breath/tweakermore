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
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.container.Slot;

import java.util.List;

public class ContainerCleaner implements IContainerProcessor
{
	@Override
	public TweakerMoreConfigBooleanHotkeyed getConfig()
	{
		return TweakerMoreConfigs.AUTO_CLEAN_CONTAINER;
	}

	@Override
	public boolean process(ClientPlayerEntity player, ContainerScreen<?> containerScreen, List<Slot> allSlots, List<Slot> playerInvSlots, List<Slot> containerInvSlots)
	{
		int counter = 0;
		for (Slot slot : containerInvSlots)
		{
			if (slot.hasStack() && TweakerMoreConfigs.AUTO_CLEAN_CONTAINER_RESTRICTION.isAllowed(slot.getStack().getItem()))
			{
				InventoryUtils.dropStack(containerScreen, slot.id);
				counter++;
			}
		}
		InfoUtils.printActionbarMessage("tweakermore.impl.autoCleanContainer.container_cleaned", counter, containerScreen.getTitle());
		return true;
	}
}
