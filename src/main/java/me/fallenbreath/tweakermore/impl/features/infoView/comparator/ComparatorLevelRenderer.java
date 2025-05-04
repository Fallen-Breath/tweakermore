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

package me.fallenbreath.tweakermore.impl.features.infoView.comparator;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.infoView.CommonScannerInfoViewer;
import me.fallenbreath.tweakermore.impl.features.infoView.cache.RenderVisitorWorldView;
import me.fallenbreath.tweakermore.util.render.TextRenderer;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.util.math.BlockPos;

public class ComparatorLevelRenderer extends CommonScannerInfoViewer
{
	private static final int FONT_COLOR = 0xFFFFFFFF;
	private static final int BACKGROUND_COLOR = 0x3F000000;

	public ComparatorLevelRenderer()
	{
		super(
				TweakerMoreConfigs.INFO_VIEW_COMPARATOR,
				TweakerMoreConfigs.INFO_VIEW_COMPARATOR_RENDER_STRATEGY,
				TweakerMoreConfigs.INFO_VIEW_COMPARATOR_TARGET_STRATEGY
		);
	}

	@Override
	public boolean shouldRenderFor(RenderVisitorWorldView world, BlockPos pos)
	{
		return world.getBlockState(pos).getBlock() instanceof ComparatorBlock && world.getBlockEntity(pos) instanceof ComparatorBlockEntity;
	}

	@Override
	public boolean requireBlockEntitySyncing(RenderVisitorWorldView world, BlockPos pos)
	{
		return true;
	}

	@Override
	protected void render(RenderContext context, RenderVisitorWorldView world, BlockPos pos, boolean isCrossHairPos)
	{
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (!(blockEntity instanceof ComparatorBlockEntity))
		{
			return;
		}

		int level = ((ComparatorBlockEntity)blockEntity).getOutputSignal();

		TextRenderer.create().
				text(String.valueOf(level)).atCenter(pos).
				fontScale(TextRenderer.DEFAULT_FONT_SCALE * TweakerMoreConfigs.INFO_VIEW_COMPARATOR_TEXT_SCALE.getDoubleValue()).
				color(FONT_COLOR, BACKGROUND_COLOR).
				shadow().seeThrough().
				render();
	}
}
