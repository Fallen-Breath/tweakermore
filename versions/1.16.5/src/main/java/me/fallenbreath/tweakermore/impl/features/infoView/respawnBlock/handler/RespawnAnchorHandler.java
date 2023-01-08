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

import me.fallenbreath.tweakermore.util.PositionUtil;
import me.fallenbreath.tweakermore.util.TemporaryBlockReplacer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RespawnAnchorHandler extends AbstractBlockHandler
{
	public RespawnAnchorHandler(World world, BlockPos blockPos, BlockState blockState)
	{
		super(world, blockPos, blockState);
	}

	@Override
	public boolean isValid()
	{
		// ref: net.minecraft.block.RespawnAnchorBlock.onUse
		if (blockState.getBlock() instanceof RespawnAnchorBlock)
		{
			return !RespawnAnchorBlock.isNether(world);
		}
		return false;
	}

	@Override
	public Vec3d getExplosionCenter()
	{
		return PositionUtil.centerOf(this.blockPos);
	}

	@Override
	public BlockPos getDeduplicationKey()
	{
		return this.blockPos;
	}

	@Override
	public void addBlocksToRemove(TemporaryBlockReplacer replacer)
	{
		replacer.add(this.blockPos, Blocks.AIR.getDefaultState());
	}

	@Override
	public float getExplosionPower()
	{
		return 5.0F;
	}
}
