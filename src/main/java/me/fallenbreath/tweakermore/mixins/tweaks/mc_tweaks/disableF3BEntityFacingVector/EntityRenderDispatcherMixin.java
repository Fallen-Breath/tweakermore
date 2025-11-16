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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableF3BEntityFacingVector;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//#if MC >= 12100
//$$ import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
//$$ import com.mojang.blaze3d.vertex.VertexConsumer;
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import net.minecraft.world.phys.Vec3;
//$$ import org.joml.Vector3f;
//#else
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin
{
	//#if MC >= 12100
	//$$ @WrapWithCondition(
	//#else
	@Inject(
	//#endif
			//#if MC >= 12105
			//$$ method = "renderHitboxesAndViewVector(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/entity/state/HitboxesRenderState;Lcom/mojang/blaze3d/vertex/VertexConsumer;F)V",
			//#else
			method = "renderHitbox",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 12103
					//$$ target = "Lnet/minecraft/client/renderer/ShapeRenderer;renderVector(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lorg/joml/Vector3f;Lnet/minecraft/world/phys/Vec3;I)V",
					//#elseif MC >= 12100
					//$$ target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderVector(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lorg/joml/Vector3f;Lnet/minecraft/world/phys/Vec3;I)V",
					//#else
					target = "Lnet/minecraft/world/entity/Entity;getViewVector(F)Lnet/minecraft/world/phys/Vec3;",
					//#endif
					ordinal = 0
			)
			//#if MC < 12100
			, cancellable = true
			//#endif
	)
	//#if MC >= 12100
	//$$ private static boolean f3BDisableFacingVector_cancelCall(PoseStack matrixStack, VertexConsumer vertexConsumer, Vector3f vector3f, Vec3 vec3d, int i)
	//$$ {
	//$$ 	return !TweakerMoreConfigs.DISABLE_F3_B_ENTITY_FACING_VECTOR.getBooleanValue();
	//$$ }
	//#else

	private
	//#if 11700 <= MC && MC < 12105
	//$$ static
	//#endif
	void f3BDisableFacingVector_cancelFollowingStuffs(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.DISABLE_F3_B_ENTITY_FACING_VECTOR.getBooleanValue())
		{
			ci.cancel();
		}
	}

	//#endif // if MC >= 12100
}