/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.util;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class PositionUtil
{
	public static Vec3d centerOf(BlockPos blockPos)
	{
		return new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
	}

	public static Collection<BlockPos> boxSurface(BlockPos pos1, BlockPos pos2)
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

	public static Collection<BlockPos> beam(Vec3d startPos, Vec3d endPos, double coneAngle, BeamMode mode)
	{
		Vec3d dir1 = endPos.subtract(startPos).normalize();
		if (dir1 == Vec3d.ZERO)
		{
			return Collections.emptyList();
		}

		double maxLen = startPos.distanceTo(endPos);
		double step = 1 / (1 + Math.sin(coneAngle));
		Long2DoubleOpenHashMap positions = new Long2DoubleOpenHashMap();

		BlockPos lastMin = null;
		BlockPos lastMax = null;
		for (double len = 0, angle = coneAngle; len < maxLen + step; len += step)
		{
			double r = len * Math.sin(angle);
			Vec3d vec3d = startPos.add(dir1.multiply(len));
			Vec3d a = vec3d.add(-r, -r, -r);
			Vec3d b = vec3d.add(+r, +r, +r);
			BlockPos pos1 = new BlockPos((int)Math.floor(a.x), (int)Math.floor(a.y),(int)Math.floor(a.z));
			BlockPos pos2 = new BlockPos((int)Math.ceil(b.x), (int)Math.ceil(b.y),(int)Math.ceil(b.z));

			if (lastMin != null)
			{
				// optimize increasing PositionUtil.boxSurface
				int minX = pos1.getX();
				int minY = pos1.getY();
				int minZ = pos1.getZ();
				int maxX = pos2.getX();
				int maxY = pos2.getY();
				int maxZ = pos2.getZ();

				// minX changed
				if (minX != lastMin.getX())
					for (int y = minY; y <= maxY; y++)
						for (int z = minZ; z <= maxZ; z++)
							positions.putIfAbsent(new BlockPos(minX, y, z).asLong(), angle);

				// maxX changed
				if (maxX != lastMax.getX())
					for (int y = minY; y <= maxY; y++)
						for (int z = minZ; z <= maxZ; z++)
							positions.putIfAbsent(new BlockPos(maxX, y, z).asLong(), angle);

				// minY changed
				if (minY != lastMin.getY())
					for (int x = minX; x <= maxX; x++)
						for (int z = minZ; z <= maxZ; z++)
							positions.putIfAbsent(new BlockPos(x, minY, z).asLong(), angle);

				// minY changed
				if (maxY != lastMax.getY())
					for (int x = minX; x <= maxX; x++)
						for (int z = minZ; z <= maxZ; z++)
							positions.putIfAbsent(new BlockPos(x, maxY, z).asLong(), angle);

				// minZ changed
				if (minZ != lastMin.getZ())
					for (int x = minX; x <= maxX; x++)
						for (int y = minY; y <= maxY; y++)
							positions.putIfAbsent(new BlockPos(x, y, minZ).asLong(), angle);

				// maxZ changed
				if (maxZ != lastMin.getZ())
					for (int x = minX; x <= maxX; x++)
						for (int y = minY; y <= maxY; y++)
							positions.putIfAbsent(new BlockPos(x, y, maxZ).asLong(), angle);
			}
			else
			{
				for (BlockPos pos : PositionUtil.boxSurface(pos1, pos2))
				{
					positions.putIfAbsent(pos.asLong(), angle);
				}
			}

			lastMin = pos1;
			lastMax = pos2;

			switch (mode)
			{
				case BEAM:
					angle = coneAngle * Math.max(1 - len / maxLen, 0);
					break;
				case CONE:
					angle = coneAngle;
					break;
			}
		}

		List<BlockPos> result = Lists.newArrayList();
		positions.forEach((l, a) -> {
			BlockPos pos = BlockPos.fromLong(l);
			Vec3d vec3d = PositionUtil.centerOf(pos).subtract(startPos);
			if (vec3d.length() <= maxLen)
			{
				Vec3d dir2 = vec3d.normalize();
				double cos = dir2.dotProduct(dir1);
				if (cos >= Math.cos(a))
				{
					result.add(pos);
				}
			}
		});
		return result;
	}

	public enum BeamMode
	{
		/**
		 *       / ---\
		 *   / ---------\
		 * x -----------|
		 *   \ ---------|
		 *       \ ---/
		 */
		BEAM,

		/**
		 *       /-|
		 *     /---|
		 *   /-----|
		 * x ------|
		 *   \-----|
		 *     \---|
		 *       \-|
		 */
		CONE
	}
}
