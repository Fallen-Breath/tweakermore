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

package me.fallenbreath.tweakermore.impl.features.infoView.growthSpeed.handlers;

import me.fallenbreath.tweakermore.impl.features.infoView.cache.RenderVisitorWorldView;
import net.minecraft.block.Block;
import net.minecraft.block.MushroomPlantBlock;
import net.minecraft.text.BaseText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static me.fallenbreath.tweakermore.util.Messenger.s;

public class SmallMushroomGrowthSpeedRenderer extends BasicGrowthSpeedRendererHandler
{
	@Override
	public boolean isTarget(Block block)
	{
		return block instanceof MushroomPlantBlock;
	}

	@Override
	public void addInfoLines(RenderVisitorWorldView world, BlockPos pos, boolean isCrossHairPos, List<BaseText> lines)
	{
		int count = countSameBlocks(world, pos);
		boolean canGrow = countSameBlocks(world, pos) < 5;
		if (isCrossHairPos)
		{
			lines.add(pair(tr("mushroom.count"), s(count, Formatting.GOLD)));
			lines.add(pair(tr("mushroom.can_grow"), bool(canGrow)));
		}
		else
		{
			lines.add(bool(canGrow));
		}
	}

	/**
	 * Reference: {@link net.minecraft.block.MushroomPlantBlock#scheduledTick} or {@link net.minecraft.block.MushroomPlantBlock#randomTick}
	 */
	private static int countSameBlocks(RenderVisitorWorldView world, BlockPos pos)
	{
		Block mushroomBlock = world.getBlockState(pos).getBlock();

		int count = 0;
		for (BlockPos blockPos : BlockPos.iterate(pos.add(-4, -1, -4), pos.add(4, 1, 4)))
		{
			if (world.getBlockState(blockPos).getBlock() == mushroomBlock)
			{
				count++;
			}
		}
		return count;
	}
}
