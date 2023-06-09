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

package me.fallenbreath.tweakermore.impl.features.autoContainerProcess;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.autoContainerProcess.processors.IProcessor;
import me.fallenbreath.tweakermore.util.StringUtil;
import me.fallenbreath.tweakermore.util.render.RenderContext;
import me.fallenbreath.tweakermore.util.render.TweakerMoreIRenderer;

import java.util.List;
import java.util.stream.Collectors;

public class AutoContainerProcessorHintRenderer implements TweakerMoreIRenderer
{
	@Override
	public void onRenderGameOverlayPost(RenderContext context)
	{
		if (!TweakerMoreConfigs.CONTAINER_PROCESSOR_HINT.getBooleanValue())
		{
			return;
		}

		List<String> lines = Lists.newArrayList();
		ContainerProcessorManager.getProcessors().forEach(processor -> {
			if (processor.isEnabled())
			{
				lines.add(GuiBase.TXT_GRAY + processor.getConfig().getConfigGuiDisplayName());
			}
		});

		if (lines.isEmpty())
		{
			return;
		}
		lines.add(0, GuiBase.TXT_UNDERLINE + StringUtils.translate("tweakermore.impl.containerProcessorHint.title"));

		RenderUtils.renderText(
				0, 0,
				TweakerMoreConfigs.CONTAINER_PROCESSOR_HINT_SCALE.getDoubleValue(),
				0xFFFFFFFF, 0x80000000,
				(HudAlignment)TweakerMoreConfigs.CONTAINER_PROCESSOR_HINT_POS.getOptionListValue(),
				false, true,
				lines
				//#if MC >= 12000
				//$$ , context.getDrawContext()
				//#elseif MC >= 11600
				//$$ , context.getMatrixStack()
				//#endif
		);
	}

	public static String modifyComment(String comment)
	{
		String lines = StringUtil.configsToListLines(
				ContainerProcessorManager.getProcessors().
						stream().
						map(IProcessor::getConfig).
						collect(Collectors.toList())
		);
		return comment + '\n' + lines;
	}
}
