package me.fallenbreath.tweakermore.util;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class TemporaryBlockReplacer
{
	private static final int SET_BLOCK_FLAGS = 16;  // no block/state/listener update

	private final World world;
	private final List<BlockPos> blockPositions = Lists.newArrayList();
	private final List<BlockState> targetStates = Lists.newArrayList();
	private final List<BlockState> originStates = Lists.newArrayList();

	public TemporaryBlockReplacer(World world)
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
			this.world.setBlockState(this.blockPositions.get(i), this.targetStates.get(i), SET_BLOCK_FLAGS);
		}
	}

	public void restoreBlocks()
	{
		for (int i = 0; i < this.blockPositions.size(); i++)
		{
			this.world.setBlockState(this.blockPositions.get(i), this.originStates.get(i), SET_BLOCK_FLAGS);
		}
	}
}
