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

package me.fallenbreath.tweakermore.impl.features.infoView.structureBlock;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.infoView.AbstractInfoViewer;
import me.fallenbreath.tweakermore.mixins.tweaks.features.infoView.structureBlock.StructureBlockScreenAccessor;
import me.fallenbreath.tweakermore.util.Messenger;
import me.fallenbreath.tweakermore.util.render.TextRenderer;
import me.fallenbreath.tweakermore.util.render.TextRenderingUtil;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.BaseText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StructureBlockContentRenderer extends AbstractInfoViewer
{
	public StructureBlockContentRenderer()
	{
		super(
				TweakerMoreConfigs.INFO_VIEW_STRUCTURE_BLOCK,
				TweakerMoreConfigs.INFO_VIEW_STRUCTURE_BLOCK_RENDER_STRATEGY,
				TweakerMoreConfigs.INFO_VIEW_STRUCTURE_BLOCK_TARGET_STRATEGY
		);
	}

	@Override
	public boolean shouldRenderFor(World world, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity)
	{
		return blockState.getBlock() instanceof StructureBlock;
	}

	@Override
	public boolean requireBlockEntitySyncing(World world, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity)
	{
		// don't sync block entity if the player is operating the structure block
		// or the player might not be able to switch the structure block mode
		Screen currentScreen = MinecraftClient.getInstance().currentScreen;
		if (currentScreen instanceof StructureBlockScreenAccessor)
		{
			//noinspection RedundantIfStatement
			if (blockPos.equals(((StructureBlockScreenAccessor)currentScreen).getStructureBlock().getPos()))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public void render(RenderContext context, World world, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity)
	{
		if (!(blockEntity instanceof StructureBlockBlockEntity))
		{
			return;
		}
		StructureBlockBlockEntity sbe = (StructureBlockBlockEntity)blockEntity;

		String structureName = sbe.getStructureName();
		BaseText nameText = null;
		if (!structureName.isEmpty())
		{
			String trimmedName = TextRenderingUtil.trim(structureName, TweakerMoreConfigs.INFO_VIEW_STRUCTURE_BLOCK_MAX_WIDTH.getIntegerValue());
			String[] parts = trimmedName.split(":", 2);
			if (parts.length == 2)
			{
				String ns = parts[0];
				String path = parts[1];
				Formatting nsColor = ns.equals("minecraft") ? Formatting.GRAY : Formatting.YELLOW;
				nameText = Messenger.c(
						Messenger.s(ns, nsColor),
						Messenger.s(":", Formatting.GRAY),
						Messenger.s(path, Formatting.AQUA)
				);
			}
			else
			{
				nameText = Messenger.s(trimmedName, Formatting.AQUA);
			}
			if (trimmedName.length() < structureName.length())
			{
				nameText = Messenger.c(nameText, Messenger.s("...", Formatting.DARK_GRAY));
			}
		}

		TextRenderer textRenderer = TextRenderer.create().
				atCenter(blockPos).
				fontScale(0.025 * TweakerMoreConfigs.INFO_VIEW_STRUCTURE_BLOCK_TEXT_SCALE.getDoubleValue()).
				bgColor(0x3F000000).
				shadow().seeThrough();

		textRenderer.addLine(Messenger.c(
				Messenger.s("["),
				Messenger.tr("structure_block.mode." + sbe.getMode().asString()),
				Messenger.s("]")
		));
		if (nameText != null)
		{
			textRenderer.addLine(nameText);
		}

		textRenderer.render();
	}
}
