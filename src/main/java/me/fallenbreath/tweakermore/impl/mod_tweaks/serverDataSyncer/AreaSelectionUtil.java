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

package me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer;

import com.google.common.collect.Sets;
import fi.dy.masa.litematica.selection.AreaSelection;
import fi.dy.masa.litematica.util.PositionUtils;
import me.fallenbreath.tweakermore.util.EntityUtils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AreaSelectionUtil
{
	/**
	 * Requires mod litematica
	 */
	public static TargetPair extractBlockEntitiesAndEntities(fi.dy.masa.litematica.selection.Box box, boolean saveableOnly)
	{
		Minecraft mc = Minecraft.getInstance();
		ClientPacketListener networkHandler = mc.getConnection();
		if (networkHandler == null || mc.player == null)
		{
			return TargetPair.none();
		}

		BlockPos pos1 = box.getPos1();
		BlockPos pos2 = box.getPos2();
		if (pos1 == null || pos2 == null)
		{
			return TargetPair.none();
		}

		int minX = Math.min(pos1.getX(), pos2.getX());
		int minY = Math.min(pos1.getY(), pos2.getY());
		int minZ = Math.min(pos1.getZ(), pos2.getZ());
		int maxX = Math.max(pos1.getX(), pos2.getX());
		int maxY = Math.max(pos1.getY(), pos2.getY());
		int maxZ = Math.max(pos1.getZ(), pos2.getZ());
		Level world = EntityUtils.getEntityWorld(mc.player);

		List<BlockPos> bePositions = BlockPos.betweenClosedStream(minX, minY, minZ, maxX, maxY, maxZ).
				map(BlockPos::immutable).
				// same check in fi.dy.masa.litematica.schematic.LitematicaSchematic.takeBlocksFromWorldWithinChunk
				filter(blockPos -> {
					BlockState blockState = world.getBlockState(blockPos);
					//#if MC >= 11700
					//$$ return blockState.hasBlockEntity();
					//#else
					return blockState.getBlock().isEntityBlock();
					//#endif
				}).
				collect(Collectors.toList());

		AABB aabb = new AABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);
		List<Entity> entities = world.getEntities((Entity)null, aabb, saveableOnly ? entity -> entity.getType().canSerialize() : null);

		return TargetPair.of(bePositions, entities);
	}

	/**
	 * Requires mod litematica
	 */
	public static TargetPair extractBlockEntitiesAndEntities(AreaSelection area, boolean saveableOnly)
	{
		Set<BlockPos> bePositions = Sets.newLinkedHashSet();
		Set<Entity> entities = Sets.newLinkedHashSet();
		PositionUtils.getValidBoxes(area).forEach(box -> {
			TargetPair pair = extractBlockEntitiesAndEntities(box, saveableOnly);
			bePositions.addAll(pair.getBlockEntityPositions());
			entities.addAll(pair.getEntities());
		});
		return TargetPair.of(bePositions, entities);
	}
}
