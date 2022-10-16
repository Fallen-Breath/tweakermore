package me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock.handler;

import me.fallenbreath.tweakermore.util.TemporaryBlockReplacer;
import net.minecraft.block.BlockState;
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
		// mc1.16+ only
		return false;
	}

	@Override
	public Vec3d getExplosionCenter()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public BlockPos getDeduplicationKey()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void addBlocksToRemove(TemporaryBlockReplacer replacer)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public float getExplosionPower()
	{
		throw new UnsupportedOperationException();
	}
}
