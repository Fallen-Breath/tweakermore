package me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock.handler;

import me.fallenbreath.tweakermore.util.TemporaryBlockReplacer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface BlockHandler
{
	boolean worksFor(World world, BlockPos blockPos, BlockState blockState);

	void addBlocksToRemove(World world, BlockPos blockPos, BlockState blockState, TemporaryBlockReplacer replacer);

	Vec3d getExplosionCenter(BlockPos blockPos);

	float getExplosionPower();
}
