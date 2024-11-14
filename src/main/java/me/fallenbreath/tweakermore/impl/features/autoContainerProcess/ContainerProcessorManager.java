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

package me.fallenbreath.tweakermore.impl.features.autoContainerProcess;

import com.google.common.collect.ImmutableList;
import me.fallenbreath.tweakermore.impl.features.autoContainerProcess.processors.*;
import me.fallenbreath.tweakermore.mixins.tweaks.features.autoContainerProcess.ItemScrollerInventoryUtilsAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;

import java.util.List;
import java.util.stream.Collectors;

public class ContainerProcessorManager
{
	private static final List<IContainerProcessor> CONTAINER_PROCESSORS = ImmutableList.of(
			new ContainerCleaner(),
			new ContainerFiller(),
			new ContainerItemPutBackProcessor(),
			new ContainerMaterialListItemCollector()
	);

	private static boolean hasTweakEnabled()
	{
		return CONTAINER_PROCESSORS.stream().anyMatch(IContainerProcessor::isEnabled);
	}

	public static List<IContainerProcessor> getProcessors()
	{
		return CONTAINER_PROCESSORS;
	}

	public static void process(Container container)
	{
		if (hasTweakEnabled())
		{
			Screen screen = MinecraftClient.getInstance().currentScreen;
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			if (player != null && screen instanceof ContainerScreen<?>)
			{
				if (player.isSpectator())
				{
					return;
				}

				ContainerScreen<?> containerScreen = (ContainerScreen<?>)screen;
				if (containerScreen.getContainer() != container || !((AutoProcessableScreen)screen).shouldProcess$TKM())
				{
					return;
				}
				if (isInBlackList(containerScreen))
				{
					return;
				}

				((AutoProcessableScreen)screen).setShouldProcess$TKM(false);
				List<Slot> allSlots = container.slots;
				List<Slot> playerInvSlots = allSlots.stream().filter(slot -> slot.inventory instanceof PlayerInventory).collect(Collectors.toList());
				if (allSlots.isEmpty() || playerInvSlots.isEmpty())
				{
					return;
				}
				List<Slot> containerInvSlots = allSlots.stream().filter(slot -> ItemScrollerInventoryUtilsAccessor.areSlotsInSameInventory(slot, allSlots.get(0))).collect(Collectors.toList());
				if (containerInvSlots.isEmpty())
				{
					return;
				}

				boolean closeGui = false;
				for (IContainerProcessor processor : CONTAINER_PROCESSORS)
				{
					if (processor.isEnabled())
					{
						ProcessResult result = processor.process(player, containerScreen, allSlots, playerInvSlots, containerInvSlots);
						closeGui |= result.closeGui;
						if (result.cancelProcessing)
						{
							break;
						}
					}
				}
				if (closeGui)
				{
					player.closeContainer();
				}
			}
		}
	}

	private static <T extends Container> boolean isInBlackList(ContainerScreen<T> containerScreen)
	{
		return
				containerScreen instanceof InventoryScreen || // not screen with inventory only (1)
				containerScreen instanceof CreativeInventoryScreen ||  // not screen with inventory only (2)
				containerScreen instanceof CraftingTableScreen ||   // not crafting table
				containerScreen instanceof MerchantScreen;  // not villager trading screen
	}
}
