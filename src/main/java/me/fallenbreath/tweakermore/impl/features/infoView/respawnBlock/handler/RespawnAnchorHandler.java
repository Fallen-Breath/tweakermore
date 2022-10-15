package me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock.handler;

import me.fallenbreath.tweakermore.util.TemporaryBlockReplacer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RespawnAnchorHandler implements BlockHandler
{
	@Override
	public boolean worksFor(World world, BlockPos blockPos, BlockState blockState)
	{
		// mc1.16+ only
		return false;
	}

	@Override
	public void addBlocksToRemove(World world, BlockPos blockPos, BlockState blockState, TemporaryBlockReplacer replacer)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Vec3d getExplosionCenter(BlockPos blockPos)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public float getExplosionPower()
	{
		throw new UnsupportedOperationException();
	}
}
