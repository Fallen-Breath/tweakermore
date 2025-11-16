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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCameraSubmersionFog;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//#if MC >= 11700
//$$ import net.minecraft.client.render.CameraSubmersionType;
//#else
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
//#endif

@Restriction(require = @Condition(ModIds.optifine))
@Mixin(FogRenderer.class)
public abstract class BackgroundRenderer_optifineMixin
{
	@Dynamic("Added by optifine")
	@ModifyExpressionValue(
			// TODO: mc1.21.6 support?
			method = "setupFog",
			at = @At(
					value = "INVOKE",
					//#if MC >= 11700
					//$$ target = "Lnet/minecraft/client/render/Camera;getSubmersionType()Lnet/minecraft/client/render/CameraSubmersionType;",
					//#else
					target = "Lnet/minecraft/client/Camera;getFluidInCamera()Lnet/minecraft/world/level/material/FluidState;",
					//#endif
					remap = true
			),
			require = 0,
			remap = false
	)
	//#if MC >= 11700
	//$$ private static CameraSubmersionType disableSubmergedFog(CameraSubmersionType cameraSubmersionType)
	//#elseif MC >= 11500
	private static FluidState disableSubmergedFog(FluidState fluidState)
	//#else
	//$$ private FluidState disableSubmergedFog(FluidState fluidState)
	//#endif
	{
		if (TweakerMoreConfigs.DISABLE_CAMERA_SUBMERSION_FOG.getBooleanValue())
		{
			//#if MC >= 11700
			//$$ return CameraSubmersionType.NONE;
			//#else
			return Fluids.EMPTY.defaultFluidState();
			//#endif
		}
		//#if MC >= 11700
		//$$ return cameraSubmersionType;
		//#else
		return fluidState;
		//#endif
	}
}
