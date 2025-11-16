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

package me.fallenbreath.tweakermore.mixins.tweaks.porting.isScrollStacksFallbackFixPorting;

import fi.dy.masa.itemscroller.util.InventoryUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = {
		@Condition(ModIds.itemscroller),
		@Condition(value = ModIds.minecraft, versionPredicates = "<1.18")
})
@Mixin(InventoryUtils.class)
public abstract class InventoryUtilsMixin
{
	@Inject(
			//#if MC >= 11600
			//$$ method = "tryMoveStacks(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/inventory/Slot;Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;ZZZ)V",
			//#else
			method = "tryMoveStacks(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/inventory/Slot;Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;ZZZ)V",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 11600
					//$$ target = "Lfi/dy/masa/itemscroller/util/InventoryUtils;clickSlotsToMoveItemsFromSlot(Lnet/minecraft/world/inventory/Slot;Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;Z)V",
					//#else
					target = "Lfi/dy/masa/itemscroller/util/InventoryUtils;clickSlotsToMoveItemsFromSlot(Lnet/minecraft/world/inventory/Slot;Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;Z)V",
					//#endif
					ordinal = 1
			),
			cancellable = true
	)
	private static void itemScrollerScrollStacksFallbackFix(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.IS_SCROLL_STACKS_FALLBACK_FIX_PORTING.getBooleanValue())
		{
			ci.cancel();
		}
	}
}
