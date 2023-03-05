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

package me.fallenbreath.tweakermore.impl.features.schematicProPlace.restrict;

import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Test if the block is interact-able
 */
public interface InteractAbleTester
{
	boolean isInteractAble(PlayerEntity player, BlockState worldState);

	default InteractAbleTester and(InteractAbleTester other)
	{
		return (player, worldState) -> this.isInteractAble(player, worldState) && other.isInteractAble(player, worldState);
	}

	static InteractAbleTester always()
	{
		return (player, worldState) -> true;
	}

	static InteractAbleTester playerCanModifyWorld()
	{
		return (player, worldState) -> player.canModifyWorld();
	}

	/**
	 * For doors / trapdoors
	 */
	static InteractAbleTester notMetal()
	{
		return (player, worldState) -> worldState.getMaterial() != Material.METAL;
	}
}
