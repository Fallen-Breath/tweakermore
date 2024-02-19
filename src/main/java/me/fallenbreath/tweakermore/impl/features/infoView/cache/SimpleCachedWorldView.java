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

package me.fallenbreath.tweakermore.impl.features.infoView.cache;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SimpleCachedWorldView implements SimpleWorldView
{
	private final World world;
	private final Long2ObjectOpenHashMap<Chunk> chunkCache = new Long2ObjectOpenHashMap<>();  // chunkPos -> chunk
	private final Long2ObjectOpenHashMap<BlockState> blockStateCache = new Long2ObjectOpenHashMap<>();  // blockPos -> blockState
	private final Long2ObjectOpenHashMap<FluidState> fluidStateCache = new Long2ObjectOpenHashMap<>();  // blockPos -> blockState
	private final Long2ObjectOpenHashMap<Optional<BlockEntity>> blockEntityCache = new Long2ObjectOpenHashMap<>();  // blockPos -> blockEntity

	public SimpleCachedWorldView(World world)
	{
		this.world = world;
	}

	public World getWorld()
	{
		return this.world;
	}

	public Chunk getChunk(BlockPos blockPos)
	{
		ChunkPos chunkPos = new ChunkPos(blockPos);
		return this.chunkCache.computeIfAbsent(
				chunkPos.toLong(),
				k -> this.world.getChunk(chunkPos.x, chunkPos.z)
		);
	}

	// ======================== Vanilla BlockView Interface ========================

	@Override
	public BlockState getBlockState(BlockPos blockPos)
	{
		return this.blockStateCache.computeIfAbsent(
				blockPos.asLong(),
				bp -> this.getChunk(blockPos).getBlockState(blockPos)
		);
	}

	@Override
	public FluidState getFluidState(BlockPos blockPos)
	{
		return this.fluidStateCache.computeIfAbsent(
				blockPos.asLong(),
				bp -> this.getChunk(blockPos).getFluidState(blockPos)
		);
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos blockPos)
	{
		return this.blockEntityCache.computeIfAbsent(
				blockPos.asLong(),
				bp -> Optional.ofNullable(this.getChunk(blockPos).getBlockEntity(blockPos))
		).orElse(null);
	}

	@Override
	public int getHeight()
	{
		return this.world.getHeight();
	}

	//#if MC >= 11700
	//$$ @Override
	//$$ public int getBottomY()
	//$$ {
	//$$ 	return this.world.getBottomY();
	//$$ }
	//#endif

	// ======================== SimpleWorldView Light Getters ========================

	@Override
	public int getLightLevel(BlockPos pos)
	{
		return this.world.getLightLevel(pos);
	}

	@Override
	public int getLightLevel(LightType type, BlockPos pos)
	{
		return this.world.getLightLevel(type, pos);
	}

	@Override
	public int getBaseLightLevel(BlockPos pos, int ambientDarkness)
	{
		//#if MC >= 11500
		return this.world.getBaseLightLevel(pos, ambientDarkness);
		//#else
		//$$ return this.world.getLightLevel(pos, ambientDarkness);
		//#endif
	}
}
