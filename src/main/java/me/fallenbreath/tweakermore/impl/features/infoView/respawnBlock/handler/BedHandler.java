package me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock.handler;

import me.fallenbreath.tweakermore.util.PositionUtil;
import me.fallenbreath.tweakermore.util.TemporaryBlockReplacer;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;

public class BedHandler implements BlockHandler
{
	private static final BlockState AIR = Blocks.AIR.getDefaultState();

	@Override
	public boolean worksFor(World world, BlockPos blockPos, BlockState blockState)
	{
		// ref: net.minecraft.block.BedBlock#onUse
		if (blockState.getBlock() instanceof BedBlock)
		{
			//#if MC >= 11600
			//$$ return !BedBlock.isOverworld(world);
			//#else
			return !world.dimension.canPlayersSleep() || world.getBiome(blockPos) == Biomes.NETHER;
			//#endif
		}
		return false;
	}

	public void addBlocksToRemove(World world, BlockPos blockPos, BlockState blockState, TemporaryBlockReplacer replacer)
	{
		replacer.add(blockPos, AIR);
		BlockPos otherHalf = blockPos.offset(blockState.get(BedBlock.FACING).getOpposite());
		if (world.getBlockState(otherHalf).getBlock() == blockState.getBlock())
		{
			replacer.add(otherHalf, AIR);
		}
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
