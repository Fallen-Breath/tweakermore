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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.tweakmUnlimitedBlockEntityRenderDistance;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin
{
	@ModifyExpressionValue(
			//#if MC >= 11500
			method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V",
			//#else
			//$$ method = "render(Lnet/minecraft/block/entity/BlockEntity;FI)V",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 11700
					//$$ target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderer;isInRenderDistance(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/util/math/Vec3d;)Z"
					//#else
					target = "Lnet/minecraft/block/entity/BlockEntity;getSquaredRenderDistance()D"
					//#endif
			)
	)
	//#if MC>= 11700
	//$$ private boolean tweakmUnlimitedBlockEntityRenderDistance(boolean inRenderDistance)
	//$$ {
	//$$ 	if (TweakerMoreConfigs.TWEAKM_UNLIMITED_BLOCK_ENTITY_RENDER_DISTANCE.getBooleanValue())
	//$$ 	{
	//$$ 		inRenderDistance = true;
	//$$ 	}
	//$$ 	return inRenderDistance;
	//$$ }
	//#else
	private double tweakmUnlimitedBlockEntityRenderDistance(double renderDistance)
	{
		if (TweakerMoreConfigs.TWEAKM_UNLIMITED_BLOCK_ENTITY_RENDER_DISTANCE.getBooleanValue())
		{
			renderDistance = Double.POSITIVE_INFINITY;
		}
		return renderDistance;
	}
	//#endif
}
