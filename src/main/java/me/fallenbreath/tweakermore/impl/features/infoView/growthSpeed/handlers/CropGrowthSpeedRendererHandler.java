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
import me.fallenbreath.tweakermore.mixins.tweaks.features.infoView.growthSpeed.AttachedStemBlockAccessor;
import me.fallenbreath.tweakermore.mixins.tweaks.features.infoView.growthSpeed.CropBlockAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static me.fallenbreath.tweakermore.util.Messenger.c;
import static me.fallenbreath.tweakermore.util.Messenger.s;

//#if MC >= 12004
//$$ import net.minecraft.core.registries.Registries;
//#endif

//#if MC >= 12001
//$$ import net.minecraft.world.level.block.PitcherCropBlock;
//$$ import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
//#endif

public class CropGrowthSpeedRendererHandler extends BasicGrowthSpeedRendererHandler
{
	@Override
	public boolean isTarget(BlockState blockState)
	{
		Block block = blockState.getBlock();
		return block instanceof CropBlock || block instanceof StemBlock || block instanceof AttachedStemBlock
				//#if MC >= 12001
				//$$ || (block instanceof PitcherCropBlock && blockState.getValue(PitcherCropBlock.HALF) == DoubleBlockHalf.LOWER)
				//#endif
				;
	}

	/**
	 * references:
	 * - {@link net.minecraft.world.level.block.CropBlock#tick} or {@link net.minecraft.world.level.block.CropBlock#randomTick}
	 * - {@link net.minecraft.world.level.block.StemBlock#tick} or {@link net.minecraft.world.level.block.StemBlock#randomTick}
	 */
	@Override
	public void addInfoLines(RenderVisitorWorldView world, BlockPos pos, boolean isCrossHairPos, List<BaseComponent> lines)
	{
		Block block = world.getBlockState(pos).getBlock();
		Block cropBlock = block;
		if (block instanceof AttachedStemBlockAccessor)
		{
			//#if MC >= 12004
			//$$ // see net.minecraft.block.AttachedStemBlock#getStateForNeighborUpdate
			//$$ var optional = world.getBestWorld().getRegistryManager().
			//$$ 		get(Registries.BLOCK).
			//$$ 		getOrEmpty(((AttachedStemBlockAccessor)block).getStemBlock());
			//$$ if (optional.isPresent())
			//$$ {
			//$$ 	cropBlock = optional.get();
			//$$ }
			//#else
			cropBlock = ((AttachedStemBlockAccessor)block).getGourdBlock().getStem();
			//#endif
		}
		float baseSpeed = CropBlockAccessor.invokeGetAvailableMoisture(cropBlock, world, pos);

		Attributes attributes = new Attributes();
		ChatFormatting color = heatColor(baseSpeed / 10.0);
		attributes.add(tr("crop.base"), s(round(baseSpeed, 6), color));

		{
			int randomBound = (int)(25.0F / baseSpeed) + 1;
			attributes.add(tr("crop.chance"), s("1/" + randomBound, color));

			int light = world.getBaseLightLevel(pos, 0);
			boolean lightToLiveOk = light >= getMinimumRequiredLightLevelToSurvive(cropBlock);
			boolean lightToGrowOk = light >= getMinimumRequiredLightLevelToGrowNaturally(cropBlock);
			boolean lightOk = lightToLiveOk && lightToGrowOk;
			ChatFormatting lightColor = lightOk ? ChatFormatting.GREEN : lightToLiveOk ? ChatFormatting.GOLD : ChatFormatting.RED;
			BaseComponent value = c(s(light + " ", lightColor), bool(lightOk, lightColor));
			attributes.add(tr("crop.light"), value, lightOk);
		}

		attributes.export(lines, isCrossHairPos);
	}

	private int getMinimumRequiredLightLevelToGrowNaturally(Block block)
	{
		// net.minecraft.block.PitcherCropBlock#randomTick  --  no check
		// net.minecraft.block.CropBlock#randomTick         --  9
		// net.minecraft.block.CropBlock#hasEnoughLightAt   --  8
		//#if MC >= 12001
		//$$ if (block instanceof PitcherCropBlock)
		//$$ {
		//$$    return 8;
		//$$ }
		//#endif
		return 9;
	}

	private int getMinimumRequiredLightLevelToSurvive(Block block)
	{
		// net.minecraft.block.CropBlock#hasEnoughLightAt
		return 8;
	}
}
