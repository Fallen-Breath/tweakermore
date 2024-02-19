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

import me.fallenbreath.tweakermore.impl.features.infoView.cache.RenderVisitorWorldView;
import me.fallenbreath.tweakermore.impl.features.infoView.cache.ScanningCache;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

public interface InfoViewer
{
	/**
	 * Enabling state check. Usually it's a config check
	 */
	boolean isRenderEnabled();

	/**
	 * Scan and collect positions to be rendered
	 */
	Map<BlockPos, Renderer> scanForRender(ScanningCache cache, Vec3d camPos, Vec3d camVec);

	/**
	 * Target filtering: custom checks
	 *
	 * @param world    The current world get from {@link fi.dy.masa.malilib.util.WorldUtils#getBestWorld}
	 * @param pos      The block pos the player looking at
	 */
	boolean shouldRenderFor(RenderVisitorWorldView world, BlockPos pos);

	/**
	 * Return trues means it needs block entity data from the server
	 */
	boolean requireBlockEntitySyncing(RenderVisitorWorldView world, BlockPos pos);

	interface Renderer
	{
		void render(RenderContext context, RenderVisitorWorldView world, BlockPos pos);
	}

	// ======== Lifecycle hooks ========

	default void onInfoViewStart()
	{
	}

	default void onInfoViewEnd()
	{
	}

	default void onClientTick()
	{
	}
}
