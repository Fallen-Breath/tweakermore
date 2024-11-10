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
import net.minecraft.block.BlockState;
import net.minecraft.text.BaseText;
import net.minecraft.util.math.BlockPos;

import java.util.List;

// impl in mc1.17+
public class CopperGrowthSpeedRendererHandler extends BasicGrowthSpeedRendererHandler
{
	@Override
	public boolean isTarget(BlockState blockState)
	{
		return false;
	}

	@Override
	public void addInfoLines(RenderVisitorWorldView world, BlockPos pos, boolean isCrossHairPos, List<BaseText> lines)
	{
	}
}
