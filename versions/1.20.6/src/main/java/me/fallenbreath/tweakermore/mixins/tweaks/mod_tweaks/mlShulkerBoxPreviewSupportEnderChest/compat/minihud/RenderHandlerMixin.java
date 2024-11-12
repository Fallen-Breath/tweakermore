/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.mlShulkerBoxPreviewSupportEnderChest.compat.minihud;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.minihud.event.RenderHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.mod_tweaks.mlShulkerBoxPreviewSupportEnderChest.EnderChestItemFetcher;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition(ModIds.minihud))
@Mixin(RenderHandler.class)
public abstract class RenderHandlerMixin
{
	// at least sakura-ryoko's fork adds these extra checks

	@ModifyExpressionValue(
			method = "onRenderTooltipLast",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/component/ComponentMap;contains(Lnet/minecraft/component/DataComponentType;)Z"
			)
	)
	private boolean mlShulkerBoxPreviewSupportEnderChest_minihudSkipCheck1(boolean contains, @Local(argsOnly = true) ItemStack stack)
	{
		return contains || EnderChestItemFetcher.enableFor(stack);
	}

	@ModifyExpressionValue(
			method = "onRenderTooltipLast",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/util/InventoryUtils;shulkerBoxHasItems(Lnet/minecraft/item/ItemStack;)Z"
			)
	)
	private boolean mlShulkerBoxPreviewSupportEnderChest_minihudSkipCheck2(boolean contains, @Local(argsOnly = true) ItemStack stack)
	{
		return contains || EnderChestItemFetcher.enableFor(stack);
	}
}
