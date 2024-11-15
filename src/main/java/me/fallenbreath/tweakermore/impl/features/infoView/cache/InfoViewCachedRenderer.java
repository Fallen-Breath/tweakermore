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
import fi.dy.masa.malilib.util.WorldUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.infoView.InfoViewer;
import me.fallenbreath.tweakermore.util.FabricUtils;
import me.fallenbreath.tweakermore.util.PositionUtils;
import me.fallenbreath.tweakermore.util.render.RenderUtils;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Function;

public class InfoViewCachedRenderer
{
	private long cacheUpdateNs = 0;
	private CacheData cacheData = null;

	private boolean isCacheInvalid()
	{
		ClientWorld clientWorld = MinecraftClient.getInstance().world;
		return clientWorld == null || this.cacheData == null || clientWorld != this.cacheData.clientWorld;
	}

	public void render(RenderContext renderContext, List<InfoViewer> viewers)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		World world = WorldUtils.getBestWorld(mc);
		World clientWorld = mc.world;
		if (world == null || clientWorld == null || mc.player == null)
		{
			return;
		}

		Vec3d camPos = mc.player.getCameraPosVec(RenderUtils.tickDelta);
		Vec3d camVec = mc.player.getRotationVec(RenderUtils.tickDelta);

		long now = System.nanoTime();
		long ups = TweakerMoreConfigs.INFO_VIEW_SCANNING_PER_SECOND.getIntegerValue();
		if (this.isCacheInvalid() || ups == 0 || this.cacheUpdateNs == 0 || now - this.cacheUpdateNs >= 1e9 / ups)
		{
			this.cacheData = new CacheData(world, clientWorld);
			this.cacheUpdateNs = now;

			RenderVisitorWorldView worldView = this.cacheData.worldView;
			Map<BlockPos, List<InfoViewer.Renderer>> renderingPositions = Maps.newHashMap();
			for (InfoViewer viewer : viewers)
			{
				viewer.scanForRender(this.cacheData.scanningCache, camPos, camVec).forEach((pos, renderer) -> {
					if (viewer.shouldRenderFor(worldView, pos))
					{
						if (!worldView.isBestWorldServerWorld() && viewer.requireBlockEntitySyncing(worldView, pos))
						{
							worldView.syncBlockEntity(pos);
						}
						renderingPositions.computeIfAbsent(pos, k -> Lists.newArrayList()).add(renderer);
					}
				});
			}

			renderingPositions.keySet().stream().
					map(pos -> Pair.of(pos, camPos.squaredDistanceTo(PositionUtils.centerOf(pos)))).
					// sort by distance in descending order, so we render block info from far to near (simulating depth test)
					sorted(Collections.reverseOrder(Comparator.comparingDouble(Pair::getSecond))).
					forEach(pair -> {
						BlockPos pos = pair.getFirst();
						this.cacheData.renderingPositions.put(pos, renderingPositions.get(pos));
					});
		}

		// debug
		if (TweakerMoreConfigs.TWEAKERMORE_DEBUG_BOOL.getBooleanValue() && FabricUtils.isDevelopmentEnvironment())
		{
			Function<BlockPos, Double> distanceGetter = pos -> camPos.distanceTo(PositionUtils.centerOf(pos));
			Collection<BlockPos> positions = this.cacheData.renderingPositions.keySet();
			double maxDis = positions.stream().map(distanceGetter).mapToDouble(x -> x).max().orElse(1);
			positions.forEach(pos -> me.fallenbreath.tweakermore.util.render.TextRenderer.create().
					color(0xFFFFFF00 | (int)(255.0 * distanceGetter.apply(pos) / maxDis)).
					atCenter(pos).text("x").render()
			);
		}

		// do the render
		this.cacheData.renderingPositions.forEach((pos, renderers) -> {
			for (InfoViewer.Renderer renderer : renderers)
			{
				renderer.render(renderContext, this.cacheData.worldView, pos);
			}
		});
	}

	public void onClientTick()
	{
		this.cacheData = null;
	}

	private static class CacheData
	{
		@SuppressWarnings("FieldCanBeLocal")
		private final World bestWorld;  // WorldUtils.getBestWorld()
		private final World clientWorld;
		private final RenderVisitorWorldView worldView;

		private final ScanningCache scanningCache;
		private final Map<BlockPos, List<InfoViewer.Renderer>> renderingPositions;

		private CacheData(World bestWorld, World clientWorld)
		{
			this.bestWorld = bestWorld;
			this.clientWorld = clientWorld;
			SimpleCachedWorldView cachedBestWorld = new SimpleCachedWorldView(bestWorld);
			SimpleCachedWorldView cachedClientWorld = clientWorld != bestWorld ? new SimpleCachedWorldView(clientWorld) : cachedBestWorld;
			this.worldView = new RenderVisitorWorldView(cachedBestWorld, cachedClientWorld);

			this.scanningCache = new ScanningCache();
			this.renderingPositions = Maps.newLinkedHashMap();
		}
	}
}
