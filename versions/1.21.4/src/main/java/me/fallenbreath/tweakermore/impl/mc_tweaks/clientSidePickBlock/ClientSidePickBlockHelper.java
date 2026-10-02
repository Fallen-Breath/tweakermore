/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.impl.mc_tweaks.clientSidePickBlock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

//#if MC >= 26.1
//$$ import net.minecraft.world.inventory.ContainerInput;
//#else
import net.minecraft.world.inventory.ClickType;
//#endif

//#if MC >= 1.21.6
//$$ import me.fallenbreath.tweakermore.TweakerMoreMod;
//$$ import net.minecraft.util.ProblemReporter;
//$$ import net.minecraft.world.level.storage.TagValueOutput;
//#else
import net.minecraft.nbt.CompoundTag;
//#endif

/**
 * mc >= 1.21.4: subproject 1.21.4                  <--------
 * mc <  1.21.4: subproject 1.15.2 (main project)
 * <p>
 * Client adaptation of vanilla picking (mc1.21.4+).
 */
public class ClientSidePickBlockHelper
{
	/**
	 * Reference: {@link net.minecraft.server.network.ServerGamePacketListenerImpl#handlePickItemFromBlock} (mc1.21.4+).
	 */
	@SuppressWarnings("deprecation")
	public static void pickBlock(Minecraft mc, BlockPos pos, boolean includeData)
	{
		if (mc.player == null || mc.level == null || mc.gameMode == null || !mc.level.hasChunkAt(pos))
		{
			return;
		}

		BlockState state = mc.level.getBlockState(pos);
		boolean copyData = mc.player.hasInfiniteMaterials() && includeData;
		ItemStack stack = state.getCloneItemStack(mc.level, pos, copyData);
		if (stack.isEmpty())
		{
			return;
		}

		stack = stack.copy();
		if (copyData && state.hasBlockEntity())
		{
			BlockEntity blockEntity = mc.level.getBlockEntity(pos);
			if (blockEntity != null)
			{
				addBlockDataToItem(stack, blockEntity, mc.level);
			}
		}
		pickItem(mc, stack);
	}

	/**
	 * Reference: {@link net.minecraft.server.network.ServerGamePacketListenerImpl#handlePickItemFromEntity} (mc1.21.4+),
	 * excluding profile output.
	 */
	public static void pickEntity(Minecraft mc, Entity entity)
	{
		if (mc.player == null || mc.level == null || mc.gameMode == null)
		{
			return;
		}

		ItemStack stack = entity.getPickResult();
		if (stack != null)
		{
			pickItem(mc, stack);
		}
	}

	/**
	 * Reference: {@link net.minecraft.server.network.ServerGamePacketListenerImpl#addBlockDataToItem}, using client-held data:
	 * CompoundTag from mc1.21.4/1.21.5; ValueOutput from mc1.21.8+.
	 */
	@SuppressWarnings("deprecation")
	private static void addBlockDataToItem(ItemStack stack, BlockEntity blockEntity, ClientLevel level)
	{
		//#if MC >= 1.21.6
		//$$ try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(blockEntity.problemPath(), TweakerMoreMod.LOGGER))
		//$$ {
		//$$ 	TagValueOutput output = TagValueOutput.createWithContext(reporter, level.registryAccess());
		//$$ 	blockEntity.saveCustomOnly(output);
		//$$ 	blockEntity.removeComponentsFromTag(output);
		//$$ 	BlockItem.setBlockEntityData(stack, blockEntity.getType(), output);
		//$$ }
		//#else
		CompoundTag tag = blockEntity.saveCustomOnly(level.registryAccess());
		blockEntity.removeComponentsFromTag(tag);
		BlockItem.setBlockEntityData(stack, blockEntity.getType(), tag);
		//#endif
		stack.applyComponents(blockEntity.collectComponents());
	}

	/**
	 * Reference: {@link net.minecraft.server.network.ServerGamePacketListenerImpl#tryPickItem} (mc1.21.4+),
	 * with client-side synchronization.
	 */
	private static void pickItem(Minecraft mc, ItemStack stack)
	{
		if (stack.isEmpty() || !stack.isItemEnabled(mc.level.enabledFeatures()))
		{
			return;
		}

		Inventory inventory = mc.player.getInventory();
		int matchingSlot = inventory.findSlotMatchingItem(stack);
		if (Inventory.isHotbarSlot(matchingSlot))
		{
			setSelectedSlot(inventory, matchingSlot);
		}
		else if (mc.player.hasInfiniteMaterials())
		{
			/**
			 * Slot diff: {@link net.minecraft.client.multiplayer.MultiPlayerGameMode#handleInventoryMouseClick} (mc1.21.4).
			 * Sync all slots changed by {@link Inventory#pickSlot} / {@link Inventory#addAndPickItem}.
			 */
			ItemStack[] previousItems = new ItemStack[Inventory.INVENTORY_SIZE];
			for (int slot = 0; slot < previousItems.length; slot++)
			{
				previousItems[slot] = inventory.getItem(slot).copy();
			}

			if (matchingSlot != -1)
			{
				inventory.pickSlot(matchingSlot);
			}
			else
			{
				inventory.addAndPickItem(stack.copy());
			}

			for (int slot = 0; slot < previousItems.length; slot++)
			{
				ItemStack currentItem = inventory.getItem(slot);
				if (!ItemStack.matches(previousItems[slot], currentItem))
				{
					/**
					 * Slot mapping: {@link net.minecraft.world.inventory.InventoryMenu} constructor /
					 * {@link net.minecraft.world.inventory.AbstractContainerMenu#addStandardInventorySlots}.
					 * Creative update: {@link Minecraft#pickBlock} (mc1.21.3).
					 */
					int menuSlot = Inventory.isHotbarSlot(slot) ? 36 + slot : slot;
					mc.gameMode.handleCreativeModeItemAdd(currentItem.copy(), menuSlot);
				}
			}
		}
		else if (matchingSlot != -1)
		{
			/**
			 * Adapt {@link Inventory#pickSlot} via {@link net.minecraft.world.inventory.AbstractContainerMenu#doClick}'s SWAP.
			 * Menu guards: {@link net.minecraft.client.multiplayer.MultiPlayerGameMode#handleInventoryMouseClick} (mc1.21.4) /
			 * {@link net.minecraft.server.network.ServerGamePacketListenerImpl#handleContainerClick}.
			 */
			if (mc.player.isSpectator() || mc.player.containerMenu != mc.player.inventoryMenu)
			{
				return;
			}
			int hotbarSlot = inventory.getSuitableHotbarSlot();
			setSelectedSlot(inventory, hotbarSlot);
			//#if MC >= 26.1
			//$$ mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, matchingSlot, hotbarSlot, ContainerInput.SWAP, mc.player);
			//#else
			mc.gameMode.handleInventoryMouseClick(mc.player.inventoryMenu.containerId, matchingSlot, hotbarSlot, ClickType.SWAP, mc.player);
			//#endif
		}
	}

	/**
	 * Reference: {@link net.minecraft.server.network.ServerGamePacketListenerImpl#tryPickItem};
	 * {@link Inventory#selected} in mc1.21.4, {@link Inventory#setSelectedSlot} in mc1.21.5+.
	 */
	private static void setSelectedSlot(Inventory inventory, int slot)
	{
		//#if MC >= 1.21.5
		//$$ inventory.setSelectedSlot(slot);
		//#else
		inventory.selected = slot;
		//#endif
	}
}
