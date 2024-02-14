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

package me.fallenbreath.tweakermore.impl.features.infoView;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import fi.dy.masa.malilib.util.WorldUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.FabricUtil;
import me.fallenbreath.tweakermore.util.PositionUtil;
import me.fallenbreath.tweakermore.util.render.RenderUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class InfoViewContextCache
{
	private long cacheUpdateNs = 0;
	private CacheData cacheData = null;

	private boolean isCacheInvalid()
	{
		ClientWorld clientWorld = MinecraftClient.getInstance().world;
		return clientWorld == null || this.cacheData == null || clientWorld != this.cacheData.clientWorld;
	}

	public void iterate(RenderVisitor renderVisitor)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		World world = WorldUtils.getBestWorld(mc);
		World clientWorld = mc.world;
		if (world == null || clientWorld == null || mc.player == null)
		{
			return;
		}

		Vec3d camPos = mc.player.getCameraPosVec(RenderUtil.tickDelta);
		Vec3d camVec = mc.player.getRotationVec(RenderUtil.tickDelta);

		long now = System.nanoTime();
		long ups = TweakerMoreConfigs.INFO_VIEW_SCANNING_PER_SECOND .getIntegerValue();
		boolean isFirstUpdate = false;
		if (this.isCacheInvalid() || ups == 0 || this.cacheUpdateNs == 0 || now - this.cacheUpdateNs >= 1e9 / ups)
		{
			isFirstUpdate = true;
			this.cacheData = null;
			this.cacheUpdateNs = now;

			double reach = TweakerMoreConfigs.INFO_VIEW_TARGET_DISTANCE.getDoubleValue();
			double angle = Math.PI * TweakerMoreConfigs.INFO_VIEW_BEAM_ANGLE.getDoubleValue() / 2 / 180;
			HitResult target = mc.player.rayTrace(reach, RenderUtil.tickDelta, false);
			BlockPos crossHairPos = target instanceof BlockHitResult ? ((BlockHitResult) target).getBlockPos() : null;

			List<BlockPos> positions = Lists.newArrayList();
			positions.addAll(PositionUtil.beam(camPos, camPos.add(camVec.normalize().multiply(reach)), angle, PositionUtil.BeamMode.BEAM));
			if (crossHairPos != null && !positions.contains(crossHairPos))
			{
				positions.add(crossHairPos);
			}

			// sort by distance in descending order, so we render block info from far to near (simulating depth test)
			List<Pair<BlockPos, Double>> posPairs = Lists.newArrayList();
			positions.forEach(pos -> posPairs.add(Pair.of(pos, camPos.squaredDistanceTo(PositionUtil.centerOf(pos)))));
			posPairs.sort(Comparator.comparingDouble(Pair::getSecond));
			Collections.reverse(posPairs);

			this.cacheData = new CacheData(world, clientWorld, crossHairPos);
			posPairs.forEach(p ->this.cacheData.positions.add(p.getFirst()));
		}

		// debug
		if (TweakerMoreConfigs.TWEAKERMORE_DEBUG_BOOL.getBooleanValue() && FabricUtil.isDevelopmentEnvironment())
		{
			Function<BlockPos, Double> distanceGetter = pos -> camPos.distanceTo(PositionUtil.centerOf(pos));
			List<BlockPos> positions = this.cacheData.positions;
			double maxDis = positions.stream().map(distanceGetter).mapToDouble(x -> x).max().orElse(1);
			positions.forEach(pos -> me.fallenbreath.tweakermore.util.render.TextRenderer.create().
					color(0xFFFFFF00 | (int)(255.0 * distanceGetter.apply(pos) / maxDis)).
					atCenter(pos).text("x").render()
			);
		}

		this.cacheData.iterate(renderVisitor, isFirstUpdate);
	}

	public void onClientTick()
	{
		if (this.isCacheInvalid())
		{
			this.cacheData = null;
		}
	}

	private static class CacheData
	{
		private final World bestWorld;  // WorldUtils.getBestWorld()
		private final World clientWorld;
		private final CachedWorldAccess bestWorldChunkCache;
		private final CachedWorldAccess clientWorldChunkCache;
		private final BlockPos crossHairPos;
		private final List<BlockPos> positions;

		private CacheData(World bestWorld, World clientWorld, BlockPos crossHairPos)
		{
			this.bestWorld = bestWorld;
			this.clientWorld = clientWorld;
			this.bestWorldChunkCache = new CachedWorldAccess(bestWorld);
			this.clientWorldChunkCache = clientWorld != bestWorld ? new CachedWorldAccess(clientWorld) : this.bestWorldChunkCache;
			this.crossHairPos = crossHairPos;
			this.positions = Lists.newArrayList();
		}

		public void iterate(RenderVisitor renderVisitor, boolean isFirstUpdate)
		{
			for (BlockPos pos : this.positions)
			{
				renderVisitor.visitBlock(
						this.bestWorld,
						pos,
						() -> this.clientWorldChunkCache.getBlockState(pos),  // use clientWorld to avoid async block get --> faster
						() -> this.bestWorldChunkCache.getBlockEntity(pos),
						pos.equals(this.crossHairPos),
						isFirstUpdate
				);
			}
		}
	}

	@FunctionalInterface
	public interface RenderVisitor
	{
		void visitBlock(World world, BlockPos blockPos, Supplier<BlockState> blockState, Supplier<BlockEntity> blockEntity, boolean isCrossHairPos, boolean isFirstUpdate);
	}
}
