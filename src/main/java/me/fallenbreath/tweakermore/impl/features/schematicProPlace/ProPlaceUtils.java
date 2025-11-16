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

package me.fallenbreath.tweakermore.impl.features.schematicProPlace;

import fi.dy.masa.litematica.materials.MaterialCache;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ProPlaceUtils
{
	/**
	 * BlockState -> ItemStack via litematica's MaterialCache
	 */
	public static ItemStack getItemForState(BlockState state, Level world, BlockPos pos)
	{
		ItemStack result = MaterialCache.getInstance().
					//#if MC >= 11500
					getRequiredBuildItemForState
					//#else
					//$$ getItemForState
					//#endif
					(state, world, pos);

		// apply our more overrides if MaterialCache cannot provide a good result
		if (result.isEmpty())
		{
			if (state.getBlock() instanceof BubbleColumnBlock)
			{
				result = new ItemStack(Items.WATER_BUCKET);
			}
		}

		return result;
	}
}
