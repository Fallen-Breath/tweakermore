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

package me.fallenbreath.tweakermore.impl.features.vehicleFakeSneaking;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.EntityUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class VehicleFakeSneakingHelper
{
	private static final double SUPPORT_CHECK_EPSILON = 1.0E-5;

	/**
	 * Applies edge protection to a player-controlled vehicle's movement.
	 *
	 * <p>The movement adjustment is adapted from vanilla
	 * {@code net.minecraft.world.entity.player.Player#maybeBackOffFromEdge} in Minecraft 1.15.2 and
	 * {@code net.minecraft.world.entity.Entity#applySneaking} in Minecraft 1.14.4.</p>
	 */
	public static Vec3 adjustMovement(Entity entity, Vec3 movement, MoverType moverType)
	{
		if (!TweakerMoreConfigs.VEHICLE_FAKE_SNEAKING.getBooleanValue() ||
				!(entity.getControllingPassenger() instanceof LocalPlayer) ||
				(moverType != MoverType.SELF && moverType != MoverType.PLAYER) ||
				movement.y > 0.0 || !isOnGround(entity))
		{
			return movement;
		}

		float maxDownStep = getMaxDownStep(entity);
		double x = movement.x;
		double z = movement.z;
		while (x != 0.0 && canFall(entity, x, 0.0, maxDownStep))
		{
			x = stepTowardsZero(x);
		}
		while (z != 0.0 && canFall(entity, 0.0, z, maxDownStep))
		{
			z = stepTowardsZero(z);
		}
		while (x != 0.0 && z != 0.0 && canFall(entity, x, z, maxDownStep))
		{
			x = stepTowardsZero(x);
			z = stepTowardsZero(z);
		}
		return new Vec3(x, movement.y, z);
	}

	private static boolean isOnGround(Entity entity)
	{
		//#if MC >= 1.20.1
		//$$ return entity.onGround();
		//#elseif MC >= 1.16
		//$$ return entity.isOnGround();
		//#else
		return entity.onGround;
		//#endif
	}

	private static float getMaxDownStep(Entity entity)
	{
		//#if MC >= 1.19.4
		//$$ return entity.maxUpStep();
		//#else
		return entity.maxUpStep;
		//#endif
	}

	private static boolean canFall(Entity entity, double x, double z, float maxDownStep)
	{
		AABB box = entity.getBoundingBox();
		AABB supportCheckBox = new AABB(
				box.minX + x + SUPPORT_CHECK_EPSILON,
				box.minY - maxDownStep - SUPPORT_CHECK_EPSILON,
				box.minZ + z + SUPPORT_CHECK_EPSILON,
				box.maxX + x - SUPPORT_CHECK_EPSILON,
				box.minY,
				box.maxZ + z - SUPPORT_CHECK_EPSILON
		);
		return EntityUtils.getEntityWorld(entity).noCollision(entity, supportCheckBox);
	}

	private static double stepTowardsZero(double value)
	{
		return Math.abs(value) <= 0.05 ? 0.0 : value - Math.copySign(0.05, value);
	}
}
