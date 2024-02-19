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

package me.fallenbreath.tweakermore.impl.features.infoView.growthSpeed;

import com.google.common.collect.Lists;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.infoView.CommonScannerInfoViewer;
import me.fallenbreath.tweakermore.impl.features.infoView.cache.RenderVisitorWorldView;
import me.fallenbreath.tweakermore.impl.features.infoView.growthSpeed.handlers.CopperGrowthSpeedRendererHandler;
import me.fallenbreath.tweakermore.impl.features.infoView.growthSpeed.handlers.CropGrowthSpeedRendererHandler;
import me.fallenbreath.tweakermore.impl.features.infoView.growthSpeed.handlers.GrowthSpeedRendererHandler;
import me.fallenbreath.tweakermore.impl.features.infoView.growthSpeed.handlers.SmallMushroomGrowthSpeedRenderer;
import me.fallenbreath.tweakermore.util.render.TextRenderer;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import net.minecraft.block.*;
import net.minecraft.text.BaseText;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class GrowthSpeedRenderer extends CommonScannerInfoViewer
{
	private final List<GrowthSpeedRendererHandler> handlers = Lists.newArrayList(
			new CopperGrowthSpeedRendererHandler(),
			new CropGrowthSpeedRendererHandler(),
			new SmallMushroomGrowthSpeedRenderer()
	);

	public GrowthSpeedRenderer()
	{
		super(TweakerMoreConfigs.INFO_VIEW_GROWTH_SPEED, TweakerMoreConfigs.INFO_VIEW_GROWTH_SPEED_RENDER_STRATEGY, TweakerMoreConfigs.INFO_VIEW_GROWTH_SPEED_TARGET_STRATEGY);
	}

	@Override
	public boolean shouldRenderFor(RenderVisitorWorldView world, BlockPos pos)
	{
		Block block = world.getBlockState(pos).getBlock();
		return this.handlers.stream().anyMatch(h -> h.isTarget(block));
	}

	@Override
	public boolean requireBlockEntitySyncing(RenderVisitorWorldView world, BlockPos pos)
	{
		return false;
	}

	@Override
	protected void render(RenderContext context, RenderVisitorWorldView world, BlockPos pos, boolean isCrossHairPos)
	{
		Block block = world.getBlockState(pos).getBlock();
		List<BaseText> lines = Lists.newArrayList();
		for (GrowthSpeedRendererHandler handler : this.handlers)
		{
			if (handler.isTarget(block))
			{
				handler.addInfoLines(world, pos, isCrossHairPos, lines);
				break;
			}
		}

		if (!lines.isEmpty())
		{
			TextRenderer textRenderer = TextRenderer.create();
			lines.forEach(textRenderer::addLine);
			textRenderer.atCenter(pos).
					fontScale(TextRenderer.DEFAULT_FONT_SCALE * TweakerMoreConfigs.INFO_VIEW_GROWTH_SPEED_TEXT_SCALE.getDoubleValue()).
					bgColor(0x3F000000).
					shadow().seeThrough().
					render();
		}
	}
}
