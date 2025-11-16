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

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Restore what {@link net.minecraft.world.entity.Entity#fromTag} does to entity position, motion and rotation
 */
public class EntityMotionStateRestorer
{
	private final Entity entity;

	private final Vec3 pos;
	private final Vec3 prevPos;
	private final Vec3 lastRenderPos;

	private final Vec3 velocity;

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

		this.pos = new Vec3(
				//#if MC >= 11500
				entity.getX(), entity.getY(), entity.getZ()
				//#else
				//$$ entity.x, entity.y, entity.z
				//#endif
		);
		this.prevPos = new Vec3(entity.xo, entity.yo, entity.zo);
		this.lastRenderPos = new Vec3(entity.xOld, entity.yOld, entity.zOld);

		this.velocity = entity.getDeltaMovement();

		//#if MC >= 11700
		//$$ this.yaw = entity.getYaw();
		//$$ this.pitch = entity.getPitch();
		//#else
		this.yaw = entity.yRot;
		this.pitch = entity.xRot;
		//#endif
		this.headYaw = entity.getYHeadRot();
		this.bodyYaw = entity instanceof LivingEntity ? ((LivingEntity)entity).yBodyRot : 0;
		this.prevYaw = entity.yRotO;
		this.prevBodyYaw = entity instanceof LivingEntity ? ((LivingEntity)entity).yBodyRotO : 0;
		this.prevHeadYaw = entity instanceof LivingEntity ? ((LivingEntity)entity).yHeadRotO : 0;
		this.prevPitch = entity.xRotO;
	}

	public void restore()
	{
		//#if MC >= 11500
		this.entity.setPosRaw(this.pos.x, this.pos.y, this.pos.z);
		//#else
		//$$ this.entity.x = this.pos.x;
		//$$ this.entity.y = this.pos.y;
		//$$ this.entity.z = this.pos.z;
		//#endif
		this.entity.xo = this.prevPos.x;
		this.entity.yo = this.prevPos.y;
		this.entity.zo = this.prevPos.z;
		this.entity.xOld = this.lastRenderPos.x;
		this.entity.yOld = this.lastRenderPos.y;
		this.entity.zOld = this.lastRenderPos.z;

		this.entity.setDeltaMovement(this.velocity);

		//#if MC >= 11700
		//$$ this.entity.setYaw(this.yaw);
		//$$ this.entity.setPitch(this.pitch);
		//#else
		this.entity.yRot = this.yaw;
		this.entity.xRot = this.pitch;
		//#endif
		this.entity.yRotO = this.prevYaw;
		this.entity.setYBodyRot(this.yaw);
		this.entity.setYHeadRot(this.headYaw);
		this.entity.xRotO = this.prevPitch;

		if (this.entity instanceof LivingEntity)
		{
			((LivingEntity)this.entity).yBodyRot = this.bodyYaw;
			((LivingEntity)this.entity).yBodyRotO = this.prevBodyYaw;
			((LivingEntity)this.entity).yHeadRotO = this.prevHeadYaw;
		}
	}
}
