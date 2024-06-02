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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.mlShulkerBoxPreviewSupportEnderChest;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;
import me.fallenbreath.tweakermore.impl.mod_tweaks.mlShulkerBoxPreviewSupportEnderChest.EnderChestItemFetcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RenderUtils.class)
public abstract class RenderUtilsMixin
{
	@ModifyExpressionValue(
			method = "renderShulkerBoxPreview",
			at = @At(
					value = "INVOKE",
					//#if MC >= 12006
					//$$ target = "Lnet/minecraft/component/ComponentMap;contains(Lnet/minecraft/component/DataComponentType;)Z",
					//#else
					target = "Lnet/minecraft/item/ItemStack;hasTag()Z",
					//#endif
					ordinal = 0
			)
	)
	private static boolean mlShulkerBoxPreviewSupportEnderChest_skipNbtCheck(boolean hasTag, ItemStack stack, int x, int y, boolean useBgColors)
	{
		return hasTag || EnderChestItemFetcher.enableFor(stack);
	}

	@ModifyVariable(
			method = "renderShulkerBoxPreview",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lfi/dy/masa/malilib/util/InventoryUtils;getStoredItems(Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/util/DefaultedList;",
					shift = At.Shift.AFTER
			)
	)
	private static DefaultedList<ItemStack> mlShulkerBoxPreviewSupportEnderChest_hackItemList(DefaultedList<ItemStack> items, ItemStack stack, int x, int y, boolean useBgColors)
	{
		if (EnderChestItemFetcher.enableFor(stack))
		{
			items = EnderChestItemFetcher.fetch().orElse(items);
		}
		return items;
	}

	@ModifyVariable(
			method = "renderShulkerBoxPreview",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lfi/dy/masa/malilib/render/InventoryOverlay;getInventoryType(Lnet/minecraft/item/ItemStack;)Lfi/dy/masa/malilib/render/InventoryOverlay$InventoryRenderType;",
					shift = At.Shift.AFTER
			)
	)
	private static InventoryOverlay.InventoryRenderType mlShulkerBoxPreviewSupportEnderChest_modifyInventoryType(InventoryOverlay.InventoryRenderType type, ItemStack stack, int x, int y, boolean useBgColors)
	{
		if (EnderChestItemFetcher.enableFor(stack))
		{
			type = InventoryOverlay.InventoryRenderType.FIXED_27;
		}
		return type;
	}
}
