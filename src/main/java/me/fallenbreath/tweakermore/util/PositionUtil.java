package me.fallenbreath.tweakermore.util;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class PositionUtil
{
	public static Vec3d centerOf(BlockPos blockPos)
	{
		return new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
	}

	public static Iterable<BlockPos> boxSurface(BlockPos pos1, BlockPos pos2)
	{
		int minX = Math.min(pos1.getX(), pos2.getX());
		int minY = Math.min(pos1.getY(), pos2.getY());
		int minZ = Math.min(pos1.getZ(), pos2.getZ());
		int maxX = Math.max(pos1.getX(), pos2.getX());
		int maxY = Math.max(pos1.getY(), pos2.getY());
		int maxZ = Math.max(pos1.getZ(), pos2.getZ());

		LongOpenHashSet set = new LongOpenHashSet();
		List<BlockPos> result = Lists.newArrayList();
		Consumer<BlockPos> storage = pos -> {
			if (set.add(pos.asLong()))
			{
				result.add(pos);
			}
		};

		for (int x = minX; x <= maxX; x++)
		{
			for (int y = minY; y <= maxY; y++)
			{
				storage.accept(new BlockPos(x, y, minZ));
				storage.accept(new BlockPos(x, y, maxZ));
			}
		}
		for (int x = minX; x <= maxX; x++)
		{
			for (int z = minZ; z <= maxZ; z++)
			{
				storage.accept(new BlockPos(x, minY, z));
				storage.accept(new BlockPos(x, maxY, z));
			}
		}
		for (int y = minY; y <= maxY; y++)
		{
			for (int z = minZ; z <= maxZ; z++)
			{
				storage.accept(new BlockPos(minX, y, z));
				storage.accept(new BlockPos(maxX, y, z));
			}
		}

		return result;
	}

	public static Iterable<BlockPos> beam(Vec3d startPos, Vec3d endPos, double maxOffsetAngle)
	{
		Vec3d dir1 = endPos.subtract(startPos).normalize();
		if (dir1 == Vec3d.ZERO)
		{
			return Collections.emptyList();
		}

		double maxLen = startPos.distanceTo(endPos);
		LongOpenHashSet positions = new LongOpenHashSet();
		for (double len = 0; len < maxLen + 0.5; len += 0.5)
		{
			double r = len * Math.sin(maxOffsetAngle);
			Vec3d vec3d = startPos.add(dir1.multiply(len));
			Vec3d a = vec3d.add(-r, -r, -r);
			Vec3d b = vec3d.add(+r, +r, +r);
			BlockPos pos1 = new BlockPos((int)Math.floor(a.x), (int)Math.floor(a.y),(int)Math.floor(a.z));
			BlockPos pos2 = new BlockPos((int)Math.ceil(b.x), (int)Math.ceil(b.y),(int)Math.ceil(b.z));
			PositionUtil.boxSurface(pos1, pos2).forEach(pos -> positions.add(pos.asLong()));
		}

		List<BlockPos> result = Lists.newArrayList();
		for (Long l : positions)
		{
			BlockPos pos = BlockPos.fromLong(l);
			Vec3d vec3d = PositionUtil.centerOf(pos).subtract(startPos);
			if (vec3d.length() <= maxLen)
			{
				Vec3d dir2 = vec3d.normalize();
				double cos = dir2.dotProduct(dir1);
				if (cos >= 0 && Math.acos(cos) <= maxOffsetAngle)
				{
					result.add(pos);
				}
			}
		}
		return result;
	}
}
