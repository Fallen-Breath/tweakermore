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
import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.malilib.render.RenderUtils;
import me.fallenbreath.tweakermore.impl.mod_tweaks.mlShulkerBoxPreviewSupportEnderChest.EnderChestItemFetcher;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//#if MC >= 1.21.11
//$$ import fi.dy.masa.malilib.render.InventoryOverlayType;
//#else
import fi.dy.masa.malilib.render.InventoryOverlay;
//#endif

@Mixin(RenderUtils.class)
public abstract class RenderUtilsMixin
{
	@ModifyExpressionValue(
			method = "renderShulkerBoxPreview",
			at = @At(
					value = "INVOKE",
					//#if MC >= 12006
					//$$ target = "Lnet/minecraft/core/component/DataComponentMap;has(Lnet/minecraft/core/component/DataComponentType;)Z",
					//#else
					target = "Lnet/minecraft/world/item/ItemStack;hasTag()Z",
					//#endif
					ordinal = 0
			)
	)
	private static boolean mlShulkerBoxPreviewSupportEnderChest_skipNbtCheck(boolean hasTag, @Local(argsOnly = true) ItemStack stack)
	{
		return hasTag || EnderChestItemFetcher.enableFor(stack);
	}

	@ModifyExpressionValue(
			method = "renderShulkerBoxPreview",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/util/InventoryUtils;getStoredItems(Lnet/minecraft/world/item/ItemStack;I)Lnet/minecraft/core/NonNullList;"
			)
	)
	private static NonNullList<ItemStack> mlShulkerBoxPreviewSupportEnderChest_hackItemList(NonNullList<ItemStack> items, @Local(argsOnly = true) ItemStack stack)
	{
		if (EnderChestItemFetcher.enableFor(stack))
		{
			items = EnderChestItemFetcher.fetch().orElse(items);
		}
		return items;
	}

	@ModifyExpressionValue(
			method = "renderShulkerBoxPreview",
			at = @At(
					value = "INVOKE",
					//#if MC >= 1.21.11
					//$$ target = "Lfi/dy/masa/malilib/render/InventoryOverlay;getInventoryType(Lnet/minecraft/world/item/ItemStack;)Lfi/dy/masa/malilib/render/InventoryOverlayType;"
					//#else
					target = "Lfi/dy/masa/malilib/render/InventoryOverlay;getInventoryType(Lnet/minecraft/world/item/ItemStack;)Lfi/dy/masa/malilib/render/InventoryOverlay$InventoryRenderType;"
					//#endif
			)
	)
	//#if MC >= 1.21.11
	//$$ private static InventoryOverlayType mlShulkerBoxPreviewSupportEnderChest_modifyInventoryType(InventoryOverlayType type, @Local(argsOnly = true) ItemStack stack)
	//#else
	private static InventoryOverlay.InventoryRenderType mlShulkerBoxPreviewSupportEnderChest_modifyInventoryType(InventoryOverlay.InventoryRenderType type, @Local(argsOnly = true) ItemStack stack)
	//#endif
	{
		if (EnderChestItemFetcher.enableFor(stack))
		{
			//#if MC >= 1.21.11
			//$$ type = InventoryOverlayType.FIXED_27;
			//#else
			type = InventoryOverlay.InventoryRenderType.FIXED_27;
			//#endif
		}
		return type;
	}
}
