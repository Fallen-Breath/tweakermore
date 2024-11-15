/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.impl.features.infoView.cache;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import me.fallenbreath.tweakermore.util.PositionUtils;
import me.fallenbreath.tweakermore.util.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ScanningCache
{
	private final Map<Double, HitResult> rayTraceCache = Maps.newHashMap();
	private final Map<BeamKey, Collection<BlockPos>> beamCache = Maps.newHashMap();
	private final Map<Pair<Vec3d, Vec3d>, Collection<BlockPos>> boxCache = Maps.newHashMap();
	private final Map<Pair<Vec3d, Double>, Collection<BlockPos>> sphereCache = Maps.newHashMap();

	@Nullable
	public HitResult crossHairTarget(double reach)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.player == null)
		{
			return null;
		}
		return this.rayTraceCache.computeIfAbsent(reach, k -> mc.player.rayTrace(reach, RenderUtils.tickDelta, false));
	}

	@Nullable
	public BlockPos crossHairTargetBlock(double reach)
	{
		HitResult hitResult = this.crossHairTarget(reach);
		return hitResult instanceof BlockHitResult ? ((BlockHitResult)hitResult).getBlockPos() : null;
	}

	public Collection<BlockPos> beam(Vec3d startPos, Vec3d endPos, double coneAngle, PositionUtils.BeamMode mode)
	{
		BeamKey key = new BeamKey(startPos, endPos, coneAngle, mode);
		return this.beamCache.computeIfAbsent(key, k -> PositionUtils.beam(startPos, endPos, coneAngle, mode));
	}

	public Collection<BlockPos> box(Vec3d center, double radius)
	{
		return this.box(center.add(-radius, -radius, -radius), center.add(radius, radius, radius));
	}

	public Collection<BlockPos> box(Vec3d pos1, Vec3d pos2)
	{
		return this.boxCache.computeIfAbsent(Pair.of(pos1, pos2), k -> {
			List<BlockPos> result = Lists.newArrayList();
			for (BlockPos pos : BlockPos.iterate(PositionUtils.floored(pos1), PositionUtils.floored(pos2)))
			{
				result.add(pos.toImmutable());
			}
			return result;
		});
	}

	public Collection<BlockPos> sphere(Vec3d center, double radius)
	{
		return this.sphereCache.computeIfAbsent(
				Pair.of(center, radius),
				k -> this.box(center, radius).stream().
						filter(pos -> PositionUtils.centerOf(pos).squaredDistanceTo(center) <= radius * radius).
						collect(Collectors.toList())
		);
	}

	private static class BeamKey
	{
		private final Vec3d startPos;
		private final Vec3d endPos;
		private final double coneAngle;
		private final PositionUtils.BeamMode mode;

		private BeamKey(Vec3d startPos, Vec3d endPos, double coneAngle, PositionUtils.BeamMode mode)
		{
			this.startPos = startPos;
			this.endPos = endPos;
			this.coneAngle = coneAngle;
			this.mode = mode;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			BeamKey key = (BeamKey)o;
			return Double.compare(coneAngle, key.coneAngle) == 0 && Objects.equals(startPos, key.startPos) && Objects.equals(endPos, key.endPos) && mode == key.mode;
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(startPos, endPos, coneAngle, mode);
		}
	}
}
