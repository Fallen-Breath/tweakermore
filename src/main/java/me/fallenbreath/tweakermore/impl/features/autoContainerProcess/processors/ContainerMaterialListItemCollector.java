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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.materials.MaterialListBase;
import fi.dy.masa.litematica.materials.MaterialListEntry;
import fi.dy.masa.litematica.materials.MaterialListUtils;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigBooleanHotkeyed;
import me.fallenbreath.tweakermore.config.options.listentries.AutoCollectMaterialListItemLogType;
import me.fallenbreath.tweakermore.mixins.tweaks.features.autoCollectMaterialListItem.MaterialListHudRendererAccessor;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;

import java.util.List;

public class ContainerMaterialListItemCollector implements IContainerProcessor
{
	@Override
	public TweakerMoreConfigBooleanHotkeyed getConfig()
	{
		return TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM;
	}

	private static void log(Message.MessageType type, String translationKey, Object... args)
	{
		if (TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM_MESSAGE_TYPE.getOptionListValue() == AutoCollectMaterialListItemLogType.FULL)
		{
			InfoUtils.showGuiOrInGameMessage(type, translationKey, args);
		}
		else
		{
			String text = type.getFormatting() + StringUtils.translate(translationKey, args) + GuiBase.TXT_RST;
			InfoUtils.printActionbarMessage(text);
		}
	}

	/**
	 * Requires litematica mod
	 */
	@Override
	public ProcessResult process(ClientPlayerEntity player, ContainerScreen<?> containerScreen, List<Slot> allSlots, List<Slot> playerInvSlots, List<Slot> containerInvSlots)
	{
		MaterialListBase materialList = DataManager.getMaterialList();
		if (materialList != null)
		{
			MaterialListHudRendererAccessor hudRendererAccessor = (MaterialListHudRendererAccessor)materialList.getHudRenderer();
			String guiTitle = containerScreen.getTitle().getString();

			// refresh before operation starts to make sure it's up-to-date
			MaterialListUtils.updateAvailableCounts(materialList.getMaterialsAll(), player);
			List<MaterialListEntry> missingOnly = materialList.getMaterialsMissingOnly(true);

			boolean summaryOnly = TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM_MESSAGE_TYPE.getOptionListValue() == AutoCollectMaterialListItemLogType.SUMMARY;
			List<String> summaries = Lists.newArrayList();

			boolean takenSomething = false;
			for (MaterialListEntry entry : missingOnly)
			{
				int missing = entry.getCountMissing() * materialList.getMultiplier() - entry.getCountAvailable();
				ItemStack stack = entry.getStack();
				if (missing <= 0)
				{
					continue;
				}
				int totalTaken = 0;
				for (Slot slot : containerInvSlots)
				{
					if (InventoryUtils.areStacksEqual(stack, slot.getStack()))
					{
						int stackAmount = slot.getStack().getCount();
						this.moveToPlayerInventory(containerScreen, playerInvSlots, slot, Math.min(missing, stackAmount));
						int moved = stackAmount - slot.getStack().getCount();
						missing -= moved;
						totalTaken += moved;
						TweakerMoreMod.LOGGER.debug("Moved {}x {} to player inventory, still miss {} items", moved, stack.getItem().getName().getString(), missing);
						if (moved == 0)
						{
							TweakerMoreMod.LOGGER.debug("Player inventory is full for item {}", stack.getItem().getName().getString());
							break;
						}
					}
				}
				if (totalTaken > 0)
				{
					if (!takenSomething && !summaryOnly)
					{
						log(Message.MessageType.INFO, "tweakermore.impl.autoCollectMaterialListItem.info.title", guiTitle);
					}
					takenSomething = true;
					String missingColor = missing == 0 ? GuiBase.TXT_GREEN : GuiBase.TXT_GOLD;
					Formatting formatting = stack.getRarity().
							//#if MC >= 12006
							//$$ getFormatting();
							//#else
							formatting;
							//#endif
					String stackName = formatting + stack.getName().getString() + GuiBase.TXT_RST;
					if (summaryOnly)
					{
						summaries.add(String.format("%s +%s", stackName, missingColor + totalTaken + GuiBase.TXT_RST));
					}
					else
					{
						log(
								Message.MessageType.INFO,
								"tweakermore.impl.autoCollectMaterialListItem.info.line",
								GuiBase.TXT_GOLD + totalTaken + GuiBase.TXT_RST,
								stackName,
								missingColor + hudRendererAccessor.invokeGetFormattedCountString(missing, stack.getMaxCount()) + GuiBase.TXT_RST
						);
					}
				}
			}

			if (takenSomething)
			{
				if (summaryOnly)
				{
					log(Message.MessageType.INFO, Joiner.on(", ").join(summaries));
				}
			}
			else
			{
				log(Message.MessageType.INFO, "tweakermore.impl.autoCollectMaterialListItem.took_nothing", guiTitle);
			}

			// refresh after operation ends
			hudRendererAccessor.setLastUpdateTime(-1);
		}
		else
		{
			log(Message.MessageType.WARNING, "tweakermore.impl.autoCollectMaterialListItem.no_material_list");
		}
		return new ProcessResult(true, TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM_CLOSE_GUI.getBooleanValue());
	}

	private void moveToPlayerInventory(ContainerScreen<?> containerScreen, List<Slot> playerInvSlots, Slot fromSlot, int amount)
	{
		ItemStack stack = fromSlot.getStack().copy();
		if (amount == stack.getCount())
		{
			InventoryUtils.shiftClickSlot(containerScreen, fromSlot.id);
			return;
		}
		else if (amount > stack.getCount())
		{
			TweakerMoreMod.LOGGER.warn("Too many items to move to player inventory, the stack {} has {} items but {} items are required", stack.getItem(), stack.getCount(), amount);
			return;
		}
		// ensure amount <= stack.getCount()

		InventoryUtils.leftClickSlot(containerScreen, fromSlot.id);
		// reversed iterating to match vanilla shift-click item putting order
		for (int idx = playerInvSlots.size() - 1; idx >= 0; idx--)
		{
			Slot slot = playerInvSlots.get(idx);
			int clickAmount = 0;
			if (slot.hasStack() && InventoryUtils.areStacksEqual(slot.getStack(), stack))
			{
				ItemStack invStack = slot.getStack();
				clickAmount = Math.min(invStack.getMaxCount() - invStack.getCount(), amount);
			}
			else if (!slot.hasStack())
			{
				clickAmount = amount;
			}
			for (int i = 0; i < clickAmount; i++) InventoryUtils.rightClickSlot(containerScreen, slot.id);
			amount -= clickAmount;
			if (amount == 0)
			{
				break;
			}
		}
		InventoryUtils.leftClickSlot(containerScreen, fromSlot.id);
		if (amount != 0)
		{
			TweakerMoreMod.LOGGER.warn("Failed to move full item stack to player inventory, {} items remains", amount);
		}
	}
}
