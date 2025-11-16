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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.f3BEntityFacingVectorLength;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

//#if MC >= 12100
//$$ import org.spongepowered.asm.mixin.injection.ModifyArg;
//#else
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
//#endif

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin
{
	//#if MC >= 12100
	//$$ @ModifyArg(
	//#else
	@ModifyExpressionValue(
	//#endif
			//#if MC >= 12105
			//$$ method = "renderHitboxesAndViewVector(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/entity/state/HitboxesRenderState;Lcom/mojang/blaze3d/vertex/VertexConsumer;F)V",
			//#else
			method = "renderHitbox",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/world/entity/Entity;getViewVector(F)Lnet/minecraft/world/phys/Vec3;",
							ordinal = 0
					)
			),
			//#endif
			at = @At(
					//#if MC >= 12100
					//$$ value = "INVOKE",
					//$$ target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;",
					//$$ ordinal = 0
					//#else
					value = "CONSTANT",
					args = "doubleValue=2.0d"
					//#endif
			)
			//#if MC < 12100
			, require = 3
			, allow = 3
			//#endif
	)
	private
	//#if MC >= 11700
	//$$ static
	//#endif
	double f3BDisableFacingVector_tweakLength(double len)
	{
		if (TweakerMoreConfigs.F3_B_ENTITY_FACING_VECTOR_LENGTH.isModified())
		{
			len = Math.max(0, TweakerMoreConfigs.F3_B_ENTITY_FACING_VECTOR_LENGTH.getDoubleValue());
		}
		return len;
	}
}