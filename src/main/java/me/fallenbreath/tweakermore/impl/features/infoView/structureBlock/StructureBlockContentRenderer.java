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
import me.fallenbreath.tweakermore.impl.features.infoView.CommonScannerInfoViewer;
import me.fallenbreath.tweakermore.impl.features.infoView.cache.RenderVisitorWorldView;
import me.fallenbreath.tweakermore.mixins.tweaks.features.infoView.structureBlock.StructureBlockScreenAccessor;
import me.fallenbreath.tweakermore.util.Messenger;
import me.fallenbreath.tweakermore.util.render.TextRenderer;
import me.fallenbreath.tweakermore.util.render.TextRenderingUtil;
import me.fallenbreath.tweakermore.util.render.context.WorldRenderContext;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;

public class StructureBlockContentRenderer extends CommonScannerInfoViewer
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
	public boolean shouldRenderFor(RenderVisitorWorldView world, BlockPos pos)
	{
		return world.getBlockState(pos).getBlock() instanceof StructureBlock;
	}

	@Override
	public boolean requireBlockEntitySyncing(RenderVisitorWorldView world, BlockPos pos)
	{
		// don't sync block entity if the player is operating the structure block
		// or the player might not be able to switch the structure block mode
		Screen currentScreen = Minecraft.getInstance().screen;
		if (currentScreen instanceof StructureBlockScreenAccessor)
		{
			//noinspection RedundantIfStatement
			if (pos.equals(((StructureBlockScreenAccessor)currentScreen).getStructureBlock().getBlockPos()))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	protected void render(WorldRenderContext context, RenderVisitorWorldView world, BlockPos pos, boolean isCrossHairPos)
	{
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (!(blockEntity instanceof StructureBlockEntity))
		{
			return;
		}
		StructureBlockEntity sbe = (StructureBlockEntity)blockEntity;

		String structureName = sbe.getStructureName();
		BaseComponent nameText = null;
		if (!structureName.isEmpty())
		{
			String trimmedName = TextRenderingUtil.trim(structureName, TweakerMoreConfigs.INFO_VIEW_STRUCTURE_BLOCK_MAX_WIDTH.getIntegerValue());
			String[] parts = trimmedName.split(":", 2);
			if (parts.length == 2)
			{
				String ns = parts[0];
				String path = parts[1];
				ChatFormatting nsColor = ns.equals("minecraft") ? ChatFormatting.GRAY : ChatFormatting.YELLOW;
				nameText = Messenger.c(
						Messenger.s(ns, nsColor),
						Messenger.s(":", ChatFormatting.GRAY),
						Messenger.s(path, ChatFormatting.AQUA)
				);
			}
			else
			{
				nameText = Messenger.s(trimmedName, ChatFormatting.AQUA);
			}
			if (trimmedName.length() < structureName.length())
			{
				nameText = Messenger.c(nameText, Messenger.s("...", ChatFormatting.DARK_GRAY));
			}
		}

		TextRenderer textRenderer = TextRenderer.create().
				atCenter(pos).
				fontScale(TextRenderer.DEFAULT_FONT_SCALE * TweakerMoreConfigs.INFO_VIEW_STRUCTURE_BLOCK_TEXT_SCALE.getDoubleValue()).
				bgColor(0x3F000000).
				shadow().seeThrough();

		textRenderer.addLine(Messenger.c(
				Messenger.s("["),
				Messenger.tr("structure_block.mode." + sbe.getMode().getSerializedName()),
				Messenger.s("]")
		));
		if (nameText != null)
		{
			textRenderer.addLine(nameText);
		}

		textRenderer.render();
	}
}
