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

package me.fallenbreath.tweakermore.impl.features.infoView.hopper;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.infoView.AbstractInfoViewer;
import me.fallenbreath.tweakermore.mixins.tweaks.features.infoView.hopper.HopperBlockEntityAccessor;
import me.fallenbreath.tweakermore.util.render.ColorHolder;
import me.fallenbreath.tweakermore.util.render.TextRenderer;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HopperCooldownRenderer extends AbstractInfoViewer
{
	public HopperCooldownRenderer()
	{
		super(
				TweakerMoreConfigs.INFO_VIEW_HOPPER,
				TweakerMoreConfigs.INFO_VIEW_HOPPER_RENDER_STRATEGY,
				TweakerMoreConfigs.INFO_VIEW_HOPPER_TARGET_STRATEGY
		);
	}

	@Override
	public boolean shouldRenderFor(World world, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity)
	{
		return blockState.getBlock() instanceof HopperBlock && blockEntity instanceof HopperBlockEntity;
	}

	@Override
	public boolean requireBlockEntitySyncing()
	{
		return true;
	}

	@Override
	public void render(RenderContext context, World world, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity)
	{
		int cooldown = ((HopperBlockEntityAccessor)blockEntity).getTransferCooldown();

		// cooldown will be -1 if the hopper block entity data is not synced
		if (cooldown < 0)
		{
			return;
		}

		double alpha = cooldown > 0 ? 1 : 0.2;
		ColorHolder color = ColorHolder.of(0xCC, 0xCC, 0xCC, (int)(0xFF * alpha));

		TextRenderer.create().
				text(String.valueOf(cooldown)).atCenter(blockPos).
				color(color.pack()).
				shadow().seeThrough().
				render();
	}
}
