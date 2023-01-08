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
import me.fallenbreath.tweakermore.util.compat.carpet.CarpetModAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity
{
	public AbstractMinecartEntityMixin(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@Shadow private int
			//#if MC >= 11500
			clientInterpolationSteps;
			//#else
			//$$ field_7669;
			//#endif

	@Inject(method = "updateTrackedPositionAndAngles", at = @At("TAIL"))
	private void disableEntityRenderInterpolation_noExtraInterpolationSteps(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate, CallbackInfo ci)
	{
		if (TweakerMoreConfigs.DISABLE_ENTITY_RENDER_INTERPOLATION.getBooleanValue())
		{
			//#if MC >= 11500
			this.clientInterpolationSteps = 1;
			//#else
			//$$ this.field_7669 = 1;
			//#endif

			// carpet's tick freeze state also freezes client world entity ticking,
			// which cancels the client-side entity position interpolation logic,
			// so we need to manually setting the entity's position & rotation to the correct values
			if (CarpetModAccess.isTickFrozen())
			{
				super.updateTrackedPositionAndAngles(x, y, z, yaw, pitch, interpolationSteps, interpolate);
			}
		}
	}
}
