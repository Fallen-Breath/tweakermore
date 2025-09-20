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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableEntityRenderInterpolation;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.disableEntityRenderInterpolation.DisableEntityRenderInterpolationHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PositionInterpolator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin
{
	@ModifyExpressionValue(
			//#if MC >= 1.21.9
			//$$ method = "updateTrackedPositionAndAngles(Ljava/util/Optional;Ljava/util/Optional;Ljava/util/Optional;)V",
			//#else
			method = "updateTrackedPositionAndAngles",
			//#endif
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/Entity;getInterpolator()Lnet/minecraft/entity/PositionInterpolator;"
			)
	)
	private PositionInterpolator disableEntityRenderInterpolation_hack(PositionInterpolator positionInterpolator)
	{
		if (TweakerMoreConfigs.DISABLE_ENTITY_RENDER_INTERPOLATION.getBooleanValue())
		{
			if (positionInterpolator != null)
			{
				positionInterpolator.setLerpDuration(0);
				if (!DisableEntityRenderInterpolationHelper.shouldUpdatePositionOrAnglesDirectly())
				{
					// adjust the codeflow in the following vanilla code
					positionInterpolator = null;
				}
			}
		}
		return positionInterpolator;
	}
}
