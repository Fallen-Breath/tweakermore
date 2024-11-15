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

package me.fallenbreath.tweakermore.impl.features.schematicProPlace;

import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.util.InventoryUtils;
import fi.dy.masa.litematica.util.ItemUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.LayerRange;
import me.fallenbreath.tweakermore.util.FabricUtils;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//#if MC >= 11600
//$$ import fi.dy.masa.litematica.config.Configs;
//$$ import net.minecraft.screen.slot.Slot;
//#endif

public class SchematicBlockPicker
{
	private static final boolean TWEAKEROO_LOADED = FabricUtils.isModLoaded(ModIds.tweakeroo);

	/**
	 * Stolen from {@link fi.dy.masa.litematica.util.WorldUtils#doSchematicWorldPickBlock}
	 */
	public static void doSchematicWorldPickBlock(MinecraftClient mc, BlockPos pos, Hand hand)
	{
		// do nothing if schematic rendering is not enabled
		if (!Configs.Visuals.ENABLE_RENDERING.getBooleanValue() || !Configs.Visuals.ENABLE_SCHEMATIC_RENDERING.getBooleanValue())
		{
			return;
		}

		World schematicWorld = SchematicWorldHandler.getSchematicWorld();
		World clientWorld = mc.world;
		if (schematicWorld != null && mc.player != null && clientWorld != null && mc.interactionManager != null)
		{
			LayerRange layerRange = DataManager.getRenderLayerRange();
			if (!layerRange.isPositionWithinRange(pos))
			{
				return;
			}
			BlockState state = schematicWorld.getBlockState(pos);
			ItemStack stack = ProPlaceUtils.getItemForState(state, schematicWorld, pos);

			//#if MC >= 11700
			//$$ InventoryUtils.schematicWorldPickBlock(stack, pos, schematicWorld, mc);
			//#endif

			if (!stack.isEmpty())
			{
				//#if MC < 11700
				PlayerInventory inv = mc.player.inventory;
				stack = stack.copy();
				if (mc.player.abilities.creativeMode)
				{
					BlockEntity te = schematicWorld.getBlockEntity(pos);
					if (GuiBase.isCtrlDown() && te != null && clientWorld.isAir(pos))
					{
						ItemUtils.storeTEInStack(stack, te);
					}

					InventoryUtils.setPickedItemToHand(stack, mc);
					mc.interactionManager.clickCreativeStack(mc.player.getStackInHand(Hand.MAIN_HAND), 36 + inv.selectedSlot);
				}
				else
				{
					int slot = inv.getSlotWithStack(stack);
					boolean shouldPick = inv.selectedSlot != slot;
					if (shouldPick && slot != -1)
					{
						InventoryUtils.setPickedItemToHand(stack, mc);
					}
					//#if MC >= 11600
					//$$ else if (slot == -1 && Configs.Generic.PICK_BLOCK_SHULKERS.getBooleanValue())
					//$$ {
					//$$ 	slot = InventoryUtils.findSlotWithBoxWithItem(mc.player.playerScreenHandler, stack, false);
					//$$ 	if (slot != -1)
					//$$ 	{
					//$$ 		ItemStack boxStack = ((Slot) mc.player.playerScreenHandler.slots.get(slot)).getStack();
					//$$ 		InventoryUtils.setPickedItemToHand(boxStack, mc);
					//$$ 	}
					//$$ }
					//#endif
				}
				//#endif  // if MC < 11700

				// so hand restore works fine
				fixTweakerooHandRestoreState(hand);
			}
		}
	}

	private static void fixTweakerooHandRestoreState(Hand hand)
	{
		if (TWEAKEROO_LOADED)
		{
			fi.dy.masa.tweakeroo.tweaks.PlacementTweaks.cacheStackInHand(hand);
		}
	}
}
