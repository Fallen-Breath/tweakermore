/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableF3BEntityFacingVectorAsArrow;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.gizmos.GizmoProperties;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityHitboxDebugRenderer.class)
public abstract class EntityHitboxDebugRendererMixin
{
	@WrapOperation(
			method = "showHitboxes",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/gizmos/Gizmos;arrow(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;I)Lnet/minecraft/gizmos/GizmoProperties;"
			)
	)
	private GizmoProperties w(Vec3 start, Vec3 end, int argb, Operation<GizmoProperties> original)
	{
		if (TweakerMoreConfigs.DISABLE_F3_B_ENTITY_FACING_VECTOR_AS_ARROW.getBooleanValue())
		{
			return Gizmos.line(start, end, argb);
		}

		// vanilla
		return original.call(start, end, argb);
	}
}
