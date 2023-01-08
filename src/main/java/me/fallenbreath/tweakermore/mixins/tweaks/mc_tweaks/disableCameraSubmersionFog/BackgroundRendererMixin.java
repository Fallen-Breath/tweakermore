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

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.render.BackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

//#if MC >= 11700
//$$ import net.minecraft.client.render.CameraSubmersionType;
//#else
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
//#endif

@Restriction(conflict = @Condition(ModIds.optifine))
@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin
{
	@ModifyVariable(
			method = "applyFog",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;"
			)
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
			//$$ cameraSubmersionType = CameraSubmersionType.NONE;
			//#else
			fluidState = Fluids.EMPTY.getDefaultState();
			//#endif
		}
		//#if MC >= 11700
		//$$ return cameraSubmersionType;
		//#else
		return fluidState;
		//#endif
	}
}
