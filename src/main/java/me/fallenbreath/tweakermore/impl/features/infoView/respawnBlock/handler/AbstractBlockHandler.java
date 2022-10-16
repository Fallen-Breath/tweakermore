package me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock.handler;

import me.fallenbreath.tweakermore.util.TemporaryBlockReplacer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class AbstractBlockHandler
{
	protected final World world;
	protected final BlockPos blockPos;
	protected final BlockState blockState;

	public AbstractBlockHandler(World world, BlockPos blockPos, BlockState blockState)
	{
		this.world = world;
		this.blockPos = blockPos;
		this.blockState = blockState;
	}

	public abstract boolean isValid();

	public abstract Vec3d getExplosionCenter();

	public abstract BlockPos getDeduplicationKey();

	public abstract void addBlocksToRemove(TemporaryBlockReplacer replacer);

	public abstract float getExplosionPower();

	public Vec3d getTextPosition()
	{
		return this.getExplosionCenter();
	}
}
