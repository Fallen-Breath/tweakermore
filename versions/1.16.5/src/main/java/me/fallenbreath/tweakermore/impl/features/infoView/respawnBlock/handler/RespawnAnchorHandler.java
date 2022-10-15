package me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock.handler;

import me.fallenbreath.tweakermore.util.PositionUtil;
import me.fallenbreath.tweakermore.util.TemporaryBlockReplacer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RespawnAnchorHandler implements BlockHandler
{
	@Override
	public boolean worksFor(World world, BlockPos blockPos, BlockState blockState)
	{
		// ref: net.minecraft.block.RespawnAnchorBlock.onUse
		if (blockState.getBlock() instanceof RespawnAnchorBlock)
		{
			return !RespawnAnchorBlock.isNether(world);
		}
		return false;
	}

	@Override
	public void addBlocksToRemove(World world, BlockPos blockPos, BlockState blockState, TemporaryBlockReplacer replacer)
	{
		replacer.add(blockPos, Blocks.AIR.getDefaultState());
	}

	@Override
	public Vec3d getExplosionCenter(BlockPos blockPos)
	{
		return PositionUtil.centerOf(blockPos);
	}

	@Override
	public float getExplosionPower()
	{
		return 5.0F;
	}
}
