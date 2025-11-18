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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.unlimitedBlockEntityRenderDistance;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin
{
	@ModifyExpressionValue(
			//#if MC >= 12109
			//$$ method = "tryExtractRenderState",
			//#elseif MC >= 11500
			method = "render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V",
			//#else
			//$$ method = "render(Lnet/minecraft/world/level/block/entity/BlockEntity;FI)V",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 11700
					//$$ target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;shouldRender(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/phys/Vec3;)Z"
					//#else
					target = "Lnet/minecraft/world/level/block/entity/BlockEntity;getViewDistance()D"
					//#endif
			)
	)
	//#if MC>= 11700
	//$$ private boolean unlimitedBlockEntityRenderDistance_impl(boolean inRenderDistance)
	//$$ {
	//$$ 	if (TweakerMoreConfigs.UNLIMITED_BLOCK_ENTITY_RENDER_DISTANCE.getBooleanValue())
	//$$ 	{
	//$$ 		inRenderDistance = true;
	//$$ 	}
	//$$ 	return inRenderDistance;
	//$$ }
	//#else
	private double unlimitedBlockEntityRenderDistance_impl(double renderDistance)
	{
		if (TweakerMoreConfigs.UNLIMITED_BLOCK_ENTITY_RENDER_DISTANCE.getBooleanValue())
		{
			renderDistance = Double.POSITIVE_INFINITY;
		}
		return renderDistance;
	}
	//#endif
}
