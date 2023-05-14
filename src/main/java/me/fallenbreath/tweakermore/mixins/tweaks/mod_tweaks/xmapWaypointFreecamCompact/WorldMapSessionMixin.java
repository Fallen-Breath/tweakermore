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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.xmapWaypointFreecamCompact;

import fi.dy.masa.tweakeroo.util.CameraEntity;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Restriction(require = {
		@Condition(ModIds.xaero_minimap),
		@Condition(ModIds.tweakeroo),
})
@Pseudo
@Mixin(targets = "xaero.common.minimap.waypoints.render.WaypointsIngameRenderer", remap = false)
public abstract class WorldMapSessionMixin
{
	/**
	 * The most accurate injection position
	 */
	@Dynamic
	@Group(min = 1, max = 2)
	@ModifyVariable(
			method = "render",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/client/MinecraftClient;getCameraEntity()Lnet/minecraft/entity/Entity;",
					remap = true
			),
			require = 0,
			remap = false
	)
	private Entity adjustCameraEntityForFreecam1(Entity cameraEntity)
	{
		return adjustCameraEntityForFreecamImpl(cameraEntity);
	}

	/**
	 * {@link #adjustCameraEntityForFreecam1} may fail if xmap choose to directly access field {@link net.minecraft.client.MinecraftClient#cameraEntity}
	 * here comes a possible fallback
	 *
	 * Since function {@link #adjustCameraEntityForFreecamImpl} is reentrant, it's ok to do ModifyVariable twice
	 */
	@Dynamic
	@Group(min = 1, max = 2)
	@ModifyVariable(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/GameRenderer;getCamera()Lnet/minecraft/client/render/Camera;",
					ordinal = 0,
					remap = true
			),
			require = 0,
			remap = false
	)
	private Entity adjustCameraEntityForFreecam2(Entity cameraEntity)
	{
		return adjustCameraEntityForFreecamImpl(cameraEntity);
	}

	private Entity adjustCameraEntityForFreecamImpl(Entity cameraEntity)
	{
		if (TweakerMoreConfigs.XMAP_WAYPOINT_FREECAM_COMPACT.getBooleanValue())
		{
			Entity freecam = CameraEntity.getCamera();
			if (freecam != null)
			{
				cameraEntity = freecam;
			}
		}
		return cameraEntity;
	}
}
