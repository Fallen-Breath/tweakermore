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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;

import java.util.List;

import static me.fallenbreath.tweakermore.util.Messenger.s;
import static me.fallenbreath.tweakermore.util.Messenger.style;

//#if MC >= 12004
//$$ import net.minecraft.world.level.block.WeatheringCopperDoorBlock;
//$$ import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
//#endif

public class CopperGrowthSpeedRendererHandler extends BasicGrowthSpeedRendererHandler
{
	@Override
	public boolean isTarget(BlockState blockState)
	{
		return blockState.getBlock() instanceof WeatheringCopper && canDegrade(blockState);
	}

	private static boolean canDegrade(BlockState blockState)
	{
		//#if MC >= 12004
		//$$ // see net.minecraft.block.WeatheringCopperDoorBlock#randomTick
		//$$ if (blockState.getBlock() instanceof WeatheringCopperDoorBlock)
		//$$ {
		//$$ 	return blockState.get(WeatheringCopperDoorBlock.HALF) == DoubleBlockHalf.LOWER;
		//$$ }
		//#endif
		return true;
	}

	/**
	 * Reference:
	 * - {@link net.minecraft.block.OxidizableBlock#randomTick}
	 * - {@link net.minecraft.block.ChangeOverTimeBlock#tickDegradation}
	 */
	@Override
	public void addInfoLines(RenderVisitorWorldView world, BlockPos pos, boolean isCrossHairPos, List<BaseComponent> lines)
	{
		Block block = world.getBlockState(pos).getBlock();
		if (!(block instanceof ChangeOverTimeBlock))
		{
			return;
		}

		final float checkChance = 0.05688889F;  // is it `(8 / 9 + 56) / 100`?
		CalcResult result = calc(world, pos, (ChangeOverTimeBlock<?>)block);
		float finalChance = result.chance * checkChance;

		BaseComponent lowerCountText = s(result.lowerCount, ChatFormatting.DARK_PURPLE);
		if (result.lowerCount > 0)
		{
			style(lowerCountText, lowerCountText.getStyle().withUnderline(true));
		}

		Attributes attr1 = new Attributes();
		Attributes attr2 = new Attributes();
		attr1.add(tr("copper.chance"), s(round(finalChance * 100, 3) + "%", ChatFormatting.YELLOW));
		attr2.add(tr("copper.lower"), lowerCountText);
		attr2.add(tr("copper.same"), s(result.sameCount, ChatFormatting.BLUE));
		attr2.add(tr("copper.higher"), s(result.higherCount, ChatFormatting.DARK_AQUA));
		attr1.export(lines, isCrossHairPos);
		attr2.export(lines, isCrossHairPos);
	}

	/**
	 * Reference: {@link net.minecraft.block.ChangeOverTimeBlock#tryDegrade}
	 */
	private static CalcResult calc(RenderVisitorWorldView world, BlockPos pos, ChangeOverTimeBlock<?> self)
	{
		int selfLevel = self.getDegradationLevel().ordinal();
		CalcResult result = new CalcResult();

		for (BlockPos blockPos : BlockPos.iterateOutwards(pos, 4, 4, 4))
		{
			int distance = blockPos.getManhattanDistance(pos);
			if (distance > 4)
			{
				break;
			}
			if (blockPos.equals(pos))
			{
				continue;
			}

			Block block = world.getBlockState(blockPos).getBlock();
			if (block instanceof ChangeOverTimeBlock)
			{
				Enum<?> enum_ = ((ChangeOverTimeBlock<?>)block).getDegradationLevel();
				if (self.getDegradationLevel().getClass() == enum_.getClass())
				{
					int otherLevel = enum_.ordinal();
					if (otherLevel < selfLevel)
					{
						result.lowerCount++;
					}
					else if (otherLevel > selfLevel)
					{
						result.higherCount++;
					}
					else
					{
						result.sameCount++;
					}
				}
			}
		}

		if (result.lowerCount == 0)
		{
			float f = (float)(result.higherCount + 1) / (float)(result.higherCount + result.sameCount + 1);
			result.chance = f * f * self.getDegradationChanceMultiplier();
		}
		else
		{
			result.chance = 0;
		}
		return result;
	}

	private static class CalcResult
	{
		public float chance = 0;
		public int lowerCount = 0;
		public int sameCount = 0;
		public int higherCount = 0;
	}
}
