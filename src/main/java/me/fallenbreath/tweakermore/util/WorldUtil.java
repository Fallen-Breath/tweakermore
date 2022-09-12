package me.fallenbreath.tweakermore.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WorldUtil
{
	/**
	 * getBlockEntity, but bypasses the on-ServerThread check,
	 * so you can use it on RenderThread
	 */
	@Nullable
	public static BlockEntity getBlockEntity(World world, BlockPos pos)
	{
		return world.getChunk(pos).getBlockEntity(pos);
	}
}
