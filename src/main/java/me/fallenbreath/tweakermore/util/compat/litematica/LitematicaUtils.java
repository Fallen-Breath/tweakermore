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

package me.fallenbreath.tweakermore.util.compat.litematica;

import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.util.RayTraceUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.util.FabricUtil;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class LitematicaUtils
{
	private static final boolean LITEMATICA_LOADED = FabricUtil.isModLoaded(ModIds.litematica);

	@Nullable
	public static World getSchematicWorld()
	{
		if (LITEMATICA_LOADED)
		{
			return SchematicWorldHandler.getSchematicWorld();
		}
		return null;
	}

	public static boolean isRenderingEnabled()
	{
		if (LITEMATICA_LOADED)
		{
			return Configs.Visuals.ENABLE_RENDERING.getBooleanValue();
		}
		return false;
	}

	@Nullable
	public static BlockPos getSchematicWorldCrosshairTargetPos(Entity cameraEntity)
	{
		if (LITEMATICA_LOADED)
		{
			// reference: fi.dy.masa.litematica.render.OverlayRenderer.renderHoverInfo
			MinecraftClient mc = MinecraftClient.getInstance();
			if (mc.world != null)
			{
				try
				{
					return Optional.ofNullable(RayTraceUtils.getGenericTrace(
									mc.world, cameraEntity, 10, true
									//#if MC >= 11600
									//$$ , Configs.InfoOverlays.INFO_OVERLAYS_TARGET_FLUIDS.getBooleanValue()
									//$$ , false  // includeVerifier
									//#endif
							))
							.map(RayTraceUtils.RayTraceWrapper::getBlockHitResult)
							.map(BlockHitResult::getBlockPos)
							.orElse(null);
				}
				catch (NoSuchMethodError e)
				{
					TweakerMoreMod.LOGGER.warn("LitematicaUtils.getSchematicWorldCrosshairTargetPos failed: {}", e.toString());
					return null;
				}
			}
		}
		return null;
	}
}
