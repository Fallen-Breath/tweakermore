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

import me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerItemContentHint.ShulkerItemContentHintRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 11904
//$$ import net.minecraft.client.util.math.MatrixStack;
//$$ import net.minecraft.world.World;
//#endif

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin
{
	@Inject(
			//#if MC >= 11904
			//$$ method = "innerRenderInGui(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V",
			//#elseif MC >= 11700
			//$$ method = "innerRenderInGui(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;IIII)V",
			//#else
			method = "renderGuiItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;II)V",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 11904
					//$$ target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItemModel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/render/model/BakedModel;)V",
					//#elseif MC >= 11700
					//$$ target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItemModel(Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/render/model/BakedModel;)V",
					//#else
					target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItemModel(Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/render/model/BakedModel;)V",
					//#endif
					shift = At.Shift.AFTER
			)
	)
	private void shulkerItemContentHint_impl(
			//#if MC >= 11904
			//$$ MatrixStack matrices,
			//#endif
			LivingEntity entity,
			//#if MC >= 11904
			//$$ World world,
			//#endif

			ItemStack itemStack, int x, int y,
			//#if MC >= 11700
			//$$ int seed, int depth,
			//#endif
			CallbackInfo ci
	)
	{
		ShulkerItemContentHintRenderer.render(
				//#if MC >= 11904
				//$$ matrices,
				//#endif
				(ItemRenderer)(Object)this, itemStack, x, y
		);
	}
}
