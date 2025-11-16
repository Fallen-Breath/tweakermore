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

package me.fallenbreath.tweakermore.mixins.tweaks.porting.lmPickBlockShulkersPorting;

import fi.dy.masa.litematica.util.InventoryUtils;
import fi.dy.masa.litematica.util.WorldUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Restriction(require = {
		@Condition(ModIds.litematica),
		@Condition(value = ModIds.minecraft, versionPredicates = "<1.16")
})
@Mixin(WorldUtils.class)
public abstract class WorldUtilsMixin
{
	@Inject(
			method = "doSchematicWorldPickBlock",
			//#if MC >= 11600
			//$$ at = @At("HEAD"),  // the mixin is not applied in 1.16+
			//#else
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I",
					ordinal = 0
			),
			//#endif
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private static void lmPickBlockShulkersPorting(
			boolean closest, Minecraft mc,
			CallbackInfoReturnable<Boolean> cir,
			BlockPos pos, Level world, BlockState state, ItemStack stack, Inventory inv, int slot
	)
	{
		if (TweakerMoreConfigs.LM_PICK_BLOCK_SHULKERS_PORTING.getBooleanValue())
		{
			if (slot == -1 && mc.player != null)
			{
				slot = findSlotWithBoxWithItem(mc.player.containerMenu, stack);

				if (slot != -1)
				{
					ItemStack boxStack = mc.player.containerMenu.slots.get(slot).getItem();
					InventoryUtils.setPickedItemToHand(boxStack, mc);
				}
			}
		}
	}

	@Unique
	private static boolean doesShulkerBoxContainItem(ItemStack stack, ItemStack referenceItem)
	{
		NonNullList<ItemStack> items = fi.dy.masa.malilib.util.InventoryUtils.getStoredItems(stack);
		return items.stream().anyMatch(item -> fi.dy.masa.malilib.util.InventoryUtils.areStacksEqual(item, referenceItem));
	}

	@Unique
	private static int findSlotWithBoxWithItem(AbstractContainerMenu container, ItemStack stackReference)
	{
		final boolean isPlayerInv = container instanceof InventoryMenu;

		for (int slotNum = 0; slotNum < container.slots.size(); slotNum ++)
		{
			Slot slot = container.slots.get(slotNum);
			if (
					(!isPlayerInv || fi.dy.masa.malilib.util.InventoryUtils.isRegularInventorySlot(slot.index, false)) &&
							doesShulkerBoxContainItem(slot.getItem(), stackReference)
			)
			{
				return slot.index;
			}
		}

		return -1;
	}
}
