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
import net.minecraft.block.AttachedStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.CropBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.text.BaseText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static me.fallenbreath.tweakermore.util.Messenger.c;
import static me.fallenbreath.tweakermore.util.Messenger.s;

//#if MC >= 12004
//$$ import net.minecraft.registry.RegistryKeys;
//#endif

public class CropGrowthSpeedRendererHandler extends BasicGrowthSpeedRendererHandler
{
	@Override
	public boolean isTarget(Block block)
	{
		return block instanceof CropBlock || block instanceof StemBlock || block instanceof AttachedStemBlock;
	}

	/**
	 * references:
	 * - {@link net.minecraft.block.CropBlock#scheduledTick} or {@link net.minecraft.block.CropBlock#randomTick}
	 * - {@link net.minecraft.block.StemBlock#scheduledTick} or {@link net.minecraft.block.StemBlock#randomTick}
	 */
	@Override
	public void addInfoLines(RenderVisitorWorldView world, BlockPos pos, boolean isCrossHairPos, List<BaseText> lines)
	{
		Block block = world.getBlockState(pos).getBlock();
		Block cropBlock = block;
		if (block instanceof AttachedStemBlockAccessor)
		{
			//#if MC >= 12004
			//$$ // see net.minecraft.block.AttachedStemBlock#getStateForNeighborUpdate
			//$$ var optional = world.getBestWorld().getRegistryManager().
			//$$ 		get(RegistryKeys.BLOCK).
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
		Formatting color = heatColor(baseSpeed / 10.0);
		attributes.add(tr("crop.base"), s(round(baseSpeed, 6), color));

		{
			int randomBound = (int)(25.0F / baseSpeed) + 1;
			attributes.add(tr("crop.chance"), s("1/" + randomBound, color));

			int light = world.getBaseLightLevel(pos, 0);
			boolean lightOk = light >= 9;
			BaseText value = c(s(light + " ", boolColor(lightOk)), bool(lightOk));
			attributes.add(tr("crop.light"), value, lightOk);
		}

		attributes.export(lines, isCrossHairPos);
	}
}
