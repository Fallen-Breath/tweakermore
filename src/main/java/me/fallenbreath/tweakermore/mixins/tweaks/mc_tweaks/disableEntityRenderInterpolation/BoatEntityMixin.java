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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableEntityRenderInterpolation;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.disableEntityRenderInterpolation.DisableEntityRenderInterpolationHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12103
//$$ import net.minecraft.entity.vehicle.AbstractBoatEntity;
//#else
import net.minecraft.entity.vehicle.BoatEntity;
//#endif

@Mixin(
		//#if MC >= 12103
		//$$ AbstractBoatEntity.class
		//#else
		BoatEntity.class
		//#endif
)
public abstract class BoatEntityMixin extends Entity
{
	public BoatEntityMixin(EntityType<?> type, World world)
	{
		super(type, world);
	}

	//#if MC >= 12004
	//$$ @Shadow private int lerpTicks;
	//#else
	@Shadow private int field_7708;
	//#endif

	@Inject(method = "updateTrackedPositionAndAngles", at = @At("TAIL"))
	private void disableEntityRenderInterpolation_noExtraInterpolationSteps(
			double x, double y, double z, float yaw, float pitch, int interpolationSteps,
			//#if MC < 12002
			boolean interpolate,
			//#endif
			CallbackInfo ci
	)
	{
		if (TweakerMoreConfigs.DISABLE_ENTITY_RENDER_INTERPOLATION.getBooleanValue())
		{
			//#if MC >= 12004
			//$$ this.lerpTicks = 1;
			//#else
			this.field_7708 = 1;
			//#endif

			if (DisableEntityRenderInterpolationHelper.shouldUpdatePositionOrAnglesDirectly())
			{
				super.updateTrackedPositionAndAngles(
						x, y, z, yaw, pitch, interpolationSteps
						//#if MC < 12002
						, interpolate
						//#endif
				);
			}
		}
	}
}
