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

package me.fallenbreath.tweakermore.impl.features.infoView;

import com.google.common.collect.Maps;
import fi.dy.masa.malilib.config.IConfigBoolean;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigOptionList;
import me.fallenbreath.tweakermore.config.options.listentries.InfoViewRenderStrategy;
import me.fallenbreath.tweakermore.config.options.listentries.InfoViewTargetStrategy;
import me.fallenbreath.tweakermore.impl.features.infoView.cache.RenderVisitorWorldView;
import me.fallenbreath.tweakermore.impl.features.infoView.cache.ScanningCache;
import me.fallenbreath.tweakermore.util.PositionUtil;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.function.Supplier;

public abstract class CommonScannerInfoViewer extends AbstractInfoViewer
{
	protected final Supplier<InfoViewTargetStrategy> targetStrategySupplier;

	public CommonScannerInfoViewer(IConfigBoolean switchConfig, Supplier<InfoViewRenderStrategy> renderStrategySupplier, Supplier<InfoViewTargetStrategy> targetStrategySupplier)
	{
		super(switchConfig, renderStrategySupplier);
		this.targetStrategySupplier = targetStrategySupplier;
	}
	public CommonScannerInfoViewer(IConfigBoolean switchConfig, TweakerMoreConfigOptionList renderStrategyOption, TweakerMoreConfigOptionList targetStrategyOption)
	{
		this(switchConfig, renderStrategyOption, () -> (InfoViewTargetStrategy)targetStrategyOption.getOptionListValue());
	}
	public CommonScannerInfoViewer(IConfigBoolean switchConfig, TweakerMoreConfigOptionList renderStrategyOption, Supplier<InfoViewTargetStrategy> targetStrategySupplier)
	{
		super(switchConfig, renderStrategyOption);
		this.targetStrategySupplier = targetStrategySupplier;
	}

	@Override
	public Map<BlockPos, Renderer> scanForRender(ScanningCache cache, Vec3d camPos, Vec3d camVec)
	{
		double reach = TweakerMoreConfigs.INFO_VIEW_TARGET_DISTANCE.getDoubleValue();
		double angle = Math.PI * TweakerMoreConfigs.INFO_VIEW_BEAM_ANGLE.getDoubleValue() / 2 / 180;

		BlockPos crossHairPos = cache.crossHairTargetBlock(reach);
		Map<BlockPos, Renderer> result = Maps.newHashMap();
		Runnable addCrossHair = () -> {
			if (crossHairPos != null)
			{
				result.put(crossHairPos, this.getRendererFor(true));
			}
		};

		switch (this.targetStrategySupplier.get())
		{
			case POINTED:
				addCrossHair.run();
				break;
			case BEAM:
				Vec3d beamEnd = camPos.add(camVec.normalize().multiply(reach));
				for (BlockPos blockPos : cache.beam(camPos, beamEnd, angle, PositionUtil.BeamMode.BEAM))
				{
					result.put(blockPos, this.getRendererFor(false));
				}
				addCrossHair.run();
				break;
			case RANGE:
				for (BlockPos pos : cache.sphere(camPos, reach))
				{
					// in the same direction of the camera vector
					if (PositionUtil.centerOf(pos).add(camPos.negate()).dotProduct(camVec) > 0)
					{
						result.put(pos, this.getRendererFor(pos.equals(crossHairPos)));
					}
				}
				break;
		}

		return result;
	}

	private Renderer getRendererFor(boolean isCrossHairPos)
	{
		return (context, world, pos) -> this.render(context, world, pos, isCrossHairPos);
	}

	protected abstract void render(RenderContext context, RenderVisitorWorldView world, BlockPos pos, boolean isCrossHairPos);
}
