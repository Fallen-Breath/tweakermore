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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.shulkerItemContentHint;

import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerBoxItemContentHint.ShulkerBoxItemContentHintRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class ItemRendererMixin
{
	@Inject(
			//#if MC >= 12106
			//$$ method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V",
			//#else
			method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/world/item/ItemStack;IIII)V",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 12106
					//$$ target = "Lnet/minecraft/client/gui/render/state/GuiRenderState;submitItem(Lnet/minecraft/client/gui/render/state/GuiItemRenderState;)V",
					//#else
					target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
					//#endif
					shift = At.Shift.AFTER
			)
	)
	private void shulkerItemContentHint_impl(
			CallbackInfo ci,
			@Local(argsOnly = true) ItemStack itemStack,
			@Local(argsOnly = true, ordinal = 0) int x,
			@Local(argsOnly = true, ordinal = 1) int y
	)
	{
		DrawContext self = (DrawContext)(Object)this;
		ShulkerBoxItemContentHintRenderer.render(
				//#if MC < 12106
				self.getMatrices(),
				//#endif
				self, itemStack, x, y
		);
	}
}
