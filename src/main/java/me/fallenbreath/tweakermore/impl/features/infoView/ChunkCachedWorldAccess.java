package me.fallenbreath.tweakermore.impl.features.infoView;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;

public class ChunkCachedWorldAccess
{
	private final World world;
	private final Long2ObjectOpenHashMap<Chunk> cache = new Long2ObjectOpenHashMap<>();  // chunkPos -> chunk

	public ChunkCachedWorldAccess(World world)
	{
		this.world = world;
	}

	public Chunk getChunk(BlockPos blockPos)
	{
		ChunkPos chunkPos = new ChunkPos(blockPos);
		return this.cache.computeIfAbsent(chunkPos.toLong(), k -> this.world.getChunk(chunkPos.x, chunkPos.z));
	}

	public BlockState getBlockState(BlockPos blockPos)
	{
		return this.getChunk(blockPos).getBlockState(blockPos);
	}

	@Nullable
	public BlockEntity getBlockEntity(BlockPos blockPos)
	{
		return this.getChunk(blockPos).getBlockEntity(blockPos);
	}
}
