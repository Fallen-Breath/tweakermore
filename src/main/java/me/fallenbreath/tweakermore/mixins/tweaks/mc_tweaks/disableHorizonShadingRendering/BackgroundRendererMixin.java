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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableHorizonShadingRendering;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * mc1.21.6-: subproject 1.15.2 (main project)        <--------
 * mc1.21.6+: subproject 1.21.8
 */
@Mixin(FogRenderer.class)
public abstract class BackgroundRendererMixin
{
	@ModifyVariable(
			//#if MC >= 12103
			//$$ method = "computeFogColor",
			//#elseif MC >= 11500
			method = "setupColor",
			//#else
			//$$ method = "setupClearColor",
			//#endif
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							//#if MC >= 11800
							//$$ target = "Lnet/minecraft/client/multiplayer/ClientLevel$ClientLevelData;getClearColorScale()F"
							//#elseif MC >= 11600
							//$$ target = "Lnet/minecraft/client/multiplayer/ClientLevel$ClientLevelData;getClearColorScale()D"
							//#else
							target = "Lnet/minecraft/world/level/dimension/Dimension;getClearColorScale()D"
							//#endif
					)
			),
			at = @At(
					value = "INVOKE",
					//#if MC >= 11900
					//$$ target = "Lnet/minecraft/client/renderer/FogRenderer;getPriorityFogFunction(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/FogRenderer$MobEffectFogFunction;"
					//#else

					target = "Lnet/minecraft/client/Camera;getEntity()Lnet/minecraft/world/entity/Entity;",
					//#if MC >= 11700
					//$$ ordinal = 1
					//#else
					ordinal = 0
					//#endif

					//#endif
			),
			//#if MC >= 12103
			//$$ ordinal = 5
			//#elseif MC >= 11800
			//$$ ordinal = 2
			//#else
			ordinal = 0
			//#endif
	)
	//#if MC >= 11800
	//$$ private static float
	//#elseif MC >= 11500
	private static double
	//#else
	//$$ private double
	//#endif
	disableHorizonShadingRendering(
			//#if MC >= 11800
			//$$ float shaderRatio
			//#else
			double shaderRatio
			//#endif
	)
	{
		if (TweakerMoreConfigs.DISABLE_HORIZON_SHADING_RENDERING.getBooleanValue())
		{
			shaderRatio = 1.0F;
		}
		return shaderRatio;
	}
}
