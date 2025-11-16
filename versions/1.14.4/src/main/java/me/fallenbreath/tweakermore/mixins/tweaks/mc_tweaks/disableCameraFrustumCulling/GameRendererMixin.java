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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCameraFrustumCulling;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.disableFrustumChunkCulling.CouldBeAlwaysVisibleFrustum;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.culling.Culler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Only used in mc1.14.4
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
{
	@ModifyVariable(
			method = "render(FJ)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/culling/Culler;prepare(DDD)V",
					shift = At.Shift.AFTER
			)
	)
	private Culler disableCameraFrustumCulling_impl(Culler visibleRegion)
	{
		if (TweakerMoreConfigs.DISABLE_CAMERA_FRUSTUM_CULLING.getBooleanValue())
		{
			if (visibleRegion instanceof CouldBeAlwaysVisibleFrustum)
			{
				boolean alwaysVisible = TweakerMoreConfigs.DISABLE_CAMERA_FRUSTUM_CULLING.getBooleanValue();
				((CouldBeAlwaysVisibleFrustum)visibleRegion).setAlwaysVisible$TKM(alwaysVisible);
			}
		}
		return visibleRegion;
	}
}