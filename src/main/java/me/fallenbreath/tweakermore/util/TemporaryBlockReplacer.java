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

package me.fallenbreath.tweakermore.util;

import com.google.common.collect.Lists;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.List;

public class TemporaryBlockReplacer
{
	private static final int SET_BLOCK_FLAGS = 16;  // no block/state/listener update

	private final Level world;
	private final List<BlockPos> blockPositions = Lists.newArrayList();
	private final List<BlockState> targetStates = Lists.newArrayList();
	private final List<BlockState> originStates = Lists.newArrayList();

	public TemporaryBlockReplacer(Level world)
	{
		this.world = world;
	}

	public void add(BlockPos pos, BlockState blockState)
	{
		this.blockPositions.add(pos);
		this.targetStates.add(blockState);
	}

	public void removeBlocks()
	{
		this.originStates.clear();
		for (BlockPos pos : this.blockPositions)
		{
			this.originStates.add(this.world.getBlockState(pos));
		}
		for (int i = 0; i < this.blockPositions.size(); i++)
		{
			this.world.setBlock(this.blockPositions.get(i), this.targetStates.get(i), SET_BLOCK_FLAGS);
		}
	}

	public void restoreBlocks()
	{
		for (int i = 0; i < this.blockPositions.size(); i++)
		{
			this.world.setBlock(this.blockPositions.get(i), this.originStates.get(i), SET_BLOCK_FLAGS);
		}
	}
}
