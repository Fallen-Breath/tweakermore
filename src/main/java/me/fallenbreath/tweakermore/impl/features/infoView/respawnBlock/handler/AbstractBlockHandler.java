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

package me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock.handler;

import me.fallenbreath.tweakermore.impl.features.infoView.cache.RenderVisitorWorldView;
import me.fallenbreath.tweakermore.util.TemporaryBlockReplacer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public abstract class AbstractBlockHandler
{
	protected final RenderVisitorWorldView world;
	protected final BlockPos blockPos;
	protected final BlockState blockState;

	public AbstractBlockHandler(RenderVisitorWorldView world, BlockPos blockPos, BlockState blockState)
	{
		this.world = world;
		this.blockPos = blockPos;
		this.blockState = blockState;
	}

	public abstract boolean isValid();

	public abstract Vec3 getExplosionCenter();

	public abstract BlockPos getDeduplicationKey();

	public abstract void addBlocksToRemove(TemporaryBlockReplacer replacer);

	public abstract float getExplosionPower();

	public Vec3 getTextPosition()
	{
		return this.getExplosionCenter();
	}
}
