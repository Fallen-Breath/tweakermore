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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.f3BEntityFacingVectorLength;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.renderer.feature.HitboxFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(HitboxFeatureRenderer.class)
public abstract class DebugHitboxCommandRendererMixin
{
	@ModifyArg(
			method = "renderDebugHitbox",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;",
					ordinal = 0
			)
	)
	private static double f3BDisableFacingVector_tweakLength(double len)
	{
		if (TweakerMoreConfigs.F3_B_ENTITY_FACING_VECTOR_LENGTH.isModified())
		{
			len = Math.max(0, TweakerMoreConfigs.F3_B_ENTITY_FACING_VECTOR_LENGTH.getDoubleValue());
		}
		return len;
	}
}
