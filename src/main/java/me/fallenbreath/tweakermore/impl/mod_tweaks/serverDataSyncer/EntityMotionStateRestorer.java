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

package me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Restore what {@link net.minecraft.entity.Entity#fromTag} does to entity position, motion and rotation
 */
public class EntityMotionStateRestorer
{
	private final Entity entity;

	private final Vec3d pos;
	private final Vec3d prevPos;
	private final Vec3d lastRenderPos;

	private final Vec3d velocity;

	private final float yaw;
	private final float headYaw;
	private final float bodyYaw;
	private final float prevYaw;
	private final float prevBodyYaw;
	private final float prevHeadYaw;
	private final float pitch;
	private final float prevPitch;

	public EntityMotionStateRestorer(Entity entity)
	{
		this.entity = entity;

		this.pos = new Vec3d(
				//#if MC >= 11500
				entity.getX(), entity.getY(), entity.getZ()
				//#else
				//$$ entity.x, entity.y, entity.z
				//#endif
		);
		this.prevPos = new Vec3d(entity.prevX, entity.prevY, entity.prevZ);
		this.lastRenderPos = new Vec3d(entity.lastRenderX, entity.lastRenderY, entity.lastRenderZ);

		this.velocity = entity.getVelocity();

		//#if MC >= 11700
		//$$ this.yaw = entity.getYaw();
		//$$ this.pitch = entity.getPitch();
		//#else
		this.yaw = entity.yaw;
		this.pitch = entity.pitch;
		//#endif
		this.headYaw = entity.getHeadYaw();
		this.bodyYaw = entity instanceof LivingEntity ? ((LivingEntity)entity).bodyYaw : 0;
		this.prevYaw = entity.prevYaw;
		this.prevBodyYaw = entity instanceof LivingEntity ? ((LivingEntity)entity).prevBodyYaw : 0;
		this.prevHeadYaw = entity instanceof LivingEntity ? ((LivingEntity)entity).prevHeadYaw : 0;
		this.prevPitch = entity.prevPitch;
	}

	public void restore()
	{
		//#if MC >= 11500
		this.entity.setPos(this.pos.x, this.pos.y, this.pos.z);
		//#else
		//$$ this.entity.x = this.pos.x;
		//$$ this.entity.y = this.pos.y;
		//$$ this.entity.z = this.pos.z;
		//#endif
		this.entity.prevX = this.prevPos.x;
		this.entity.prevY = this.prevPos.y;
		this.entity.prevZ = this.prevPos.z;
		this.entity.lastRenderX = this.lastRenderPos.x;
		this.entity.lastRenderY = this.lastRenderPos.y;
		this.entity.lastRenderZ = this.lastRenderPos.z;

		this.entity.setVelocity(this.velocity);

		//#if MC >= 11700
		//$$ this.entity.setYaw(this.yaw);
		//$$ this.entity.setPitch(this.pitch);
		//#else
		this.entity.yaw = this.yaw;
		this.entity.pitch = this.pitch;
		//#endif
		this.entity.prevYaw = this.prevYaw;
		this.entity.setYaw(this.yaw);
		this.entity.setHeadYaw(this.headYaw);
		this.entity.prevPitch = this.prevPitch;

		if (this.entity instanceof LivingEntity)
		{
			((LivingEntity)this.entity).bodyYaw = this.bodyYaw;
			((LivingEntity)this.entity).prevBodyYaw = this.prevBodyYaw;
			((LivingEntity)this.entity).prevHeadYaw = this.prevHeadYaw;
		}
	}
}
