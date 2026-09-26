/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableEntityRenderInterpolation;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.disableEntityRenderInterpolation.DisableEntityRenderInterpolationHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.LinearInterpolationHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * MC 26.3 moved the interpolation/direct-update decision into the handler return value.
 */
@Mixin(Entity.class)
public abstract class EntityMixin
{
	@ModifyExpressionValue(
			method = "moveOrInterpolateTo(Lnet/minecraft/world/entity/PositionPath;FFZ)V",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/entity/Entity;interpolationHandler:Lnet/minecraft/world/entity/InterpolationHandler;"
			)
	)
	private InterpolationHandler disableEntityRenderInterpolation_setLength(InterpolationHandler interpolationHandler)
	{
		if (TweakerMoreConfigs.DISABLE_ENTITY_RENDER_INTERPOLATION.getBooleanValue()
				&& interpolationHandler instanceof LinearInterpolationHandler linearInterpolationHandler)
		{
			linearInterpolationHandler.setInterpolationLength(0);
		}
		return interpolationHandler;
	}

	@ModifyExpressionValue(
			method = "moveOrInterpolateTo(Lnet/minecraft/world/entity/PositionPath;FFZ)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/InterpolationHandler;interpolateTo(Lnet/minecraft/world/entity/PositionPath;FFZ)Z"
			)
	)
	private boolean disableEntityRenderInterpolation_directUpdate(boolean shouldInterpolate)
	{
		return TweakerMoreConfigs.DISABLE_ENTITY_RENDER_INTERPOLATION.getBooleanValue()
				&& !DisableEntityRenderInterpolationHelper.shouldUpdatePositionOrAnglesDirectly()
				? false
				: shouldInterpolate;
	}
}
